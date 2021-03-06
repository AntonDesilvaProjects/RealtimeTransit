package com.transit.dao.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.protobuf.ExtensionRegistry;
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtimeNYCT;
import com.transit.TransitConstants;
import com.transit.dao.MTASubwayDao;
import com.transit.domain.mta.subway.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import javax.xml.parsers.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MTASubwayGTFSDaoImpl implements MTASubwayDao {

    @Value("${mta.api.subway.feed-base-url}")
    private String MTA_SUBWAY_FEED_BASE_URL;

    @Value("${mta.api.subway.stations-url}")
    private String MTA_SUBWAY_STATIONS_URL;

    @Value("${mta.api.subway.status-url}")
    private String MTA_SUBWAY_STATUS_URL;

    @Value("${mta.api.subway.key}")
    private String MTA_SUBWAY_FEED_API_KEY;

    private final String STATION_ID = "Station ID";
    private final String GTFS_STOP_ID = "GTFS Stop ID";
    private final String STOP_NAME = "Stop Name";
    private final String BOROUGH = "Borough";
    private final String DAYTIME_ROUTES = "Daytime Routes";
    private final String GTFS_LATITUDE = "GTFS Latitude";
    private final String GTFS_LONGITUDE = "GTFS Longitude";

    private final String SUBWAY_STATION_KEY = "station_key";

    private ExtensionRegistry gtfsExtensionRegistry;
    private LoadingCache<String, Map<String, SubwayStation>> subwayStationCache;

    private final Function<Node, SubwayStatusResponse.SubwayStatus> mapSituationNodeToSubwayStatus = situationNode -> {
        SubwayStatusResponse.SubwayStatus status = new SubwayStatusResponse.SubwayStatus();
        NodeList childNodes = situationNode.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);
            String nodeValue = childNode.getTextContent();
            switch (childNode.getNodeName()) {
                case "CreationTime" :
                    status.setTime(nodeValue);
                    break;
                case "Summary":
                    status.setSummary(nodeValue);
                    break;
                case "Description":
                    status.setDescription(nodeValue);
                    break;
                case "LongDescription" :
                    status.setLongDescription(nodeValue);
                    break;
                case "Planned" :
                    status.setPlanned(Boolean.parseBoolean(nodeValue));
                    break;
                case "ReasonName" :
                    status.setReasonName(nodeValue);
                    break;
                case "Affects":
                    NodeList vehicleJourneyNodeList = childNode.getChildNodes(); //get the VehicleJourney Objects
                    List<SubwayStatusResponse.SubwayStatus.AffectedTrip> affectedTrips = new ArrayList<>();
                    for (int k = 0; k < vehicleJourneyNodeList.getLength(); k++) {
                        Node vehicleJourneyNode = vehicleJourneyNodeList.item(k);
                        NodeList affectedJourneyNodeList = vehicleJourneyNode.getChildNodes(); //get the AffectedVehicleJourney Objects

                        for (int l = 0; l < affectedJourneyNodeList.getLength(); l++) {
                            Node affectedJourneyNode = affectedJourneyNodeList.item(l);
                            NodeList affectedJourneyChildNodes = affectedJourneyNode.getChildNodes(); //get the individual tags of the AffectedVehicleJourney
                            SubwayStatusResponse.SubwayStatus.AffectedTrip affectedTrip = new SubwayStatusResponse.SubwayStatus.AffectedTrip();

                            for (int u = 0; u < affectedJourneyChildNodes.getLength(); u++) {
                                Node affectedJourneyInfoNode = affectedJourneyChildNodes.item(u);
                                String affectedJourneyInfoNodeValue = affectedJourneyInfoNode.getTextContent();
                                switch (affectedJourneyInfoNode.getNodeName()) {
                                    case "LineRef":
                                        affectedTrip.setRouteId(affectedJourneyInfoNodeValue);
                                        break;
                                    case "DirectionRef":
                                        affectedTrip.setDirection("1".equals(affectedJourneyInfoNodeValue) ? Trip.Direction.SOUTH : Trip.Direction.NORTH);
                                }
                            }
                            if (StringUtils.isNotEmpty(affectedTrip.getRouteId())) {
                                affectedTrip.setRouteId(affectedTrip.getRouteId().replace("\n", "").trim());
                                affectedTrips.add(affectedTrip);
                            }
                        }
                    }
                    status.setAffectedTrips(affectedTrips);
                    break;
            }
        }
        return status;
    };

    @PostConstruct
    public void init() {
        subwayStationCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<String, Map<String, SubwayStation>>() {
                        @Override
                        public Map<String, SubwayStation> load(String key) throws Exception {
                            return fetchSubwayStations().stream().collect(Collectors.toMap(SubwayStation::getGtfsStopId, Function.identity()));
                        }
                    });
    }

    @Override
    public List<Feed> getAvailableFeeds() {
        //there is no dynamic way to pull this data so return a pre-built list
        return new ArrayList<>(TransitConstants.FEED_ID_MAP.values());
    }

    @Override
    public List<Trip> getTripsForFeed(int feedId) {
        List<Trip> trips = new ArrayList<>();
        try (InputStream urlStream = buildFeedUrl(feedId).openStream()) {
            Map<String, SubwayStation> stationIdMap = subwayStationCache.get(SUBWAY_STATION_KEY);
            GtfsRealtime.FeedMessage feedMessage = GtfsRealtime.FeedMessage.parseFrom(urlStream, getGtfsExtensionRegistry());
            for (GtfsRealtime.FeedEntity entity : feedMessage.getEntityList()) {
                if (entity.hasTripUpdate()) {
                    GtfsRealtime.TripUpdate gtfsTripUpdate = entity.getTripUpdate();
                    GtfsRealtime.TripDescriptor tripDescriptor = gtfsTripUpdate.getTrip();
                    List<GtfsRealtime.TripUpdate.StopTimeUpdate> updateList = gtfsTripUpdate.getStopTimeUpdateList();
                    List<TripUpdate> tripUpdates = updateList.stream().map(u -> {
                        String stationId = u.getStopId().substring(0, u.getStopId().length() - 1);
                        final SubwayStation station = stationIdMap.get(stationId);
                        if (station != null) {
                            return new TripUpdate.builder()
                                    .forSubwayStation(station)
                                    .arrivingOn(u.getArrival().getTime() * 1000)
                                    .departingOn(u.getDeparture().getTime() * 1000).build();
                        } else {
                            System.err.println("Unable to find a station for id " + stationId + " in route " + tripDescriptor.getRouteId());
                            return null;
                        }
                    }).filter(Objects::nonNull).collect(Collectors.toList());

                    //get the NYCT trip descriptor get additional fields from MTA
                    GtfsRealtimeNYCT.NyctTripDescriptor nyctTripDescriptor = (GtfsRealtimeNYCT.NyctTripDescriptor) tripDescriptor.getField(GtfsRealtimeNYCT.nyctTripDescriptor.getDescriptor());
                    Trip trip = new Trip.builder(tripDescriptor.getTripId())
                            .withRouteId(tripDescriptor.getRouteId())
                            .withTrainId(nyctTripDescriptor.getTrainId())
                            .headed(Trip.Direction.fromMTADirection(nyctTripDescriptor.getDirection()))
                            .startingAt(tripDescriptor.getStartTime())
                            .startingOn(tripDescriptor.getStartDate())
                            .havingTripUpdates(tripUpdates)
                            .build();
                    trips.add(trip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trips;
    }

    @Override
    public List<SubwayStation> getSubwayStations() {
        List<SubwayStation> subwayStations = null;
        try {
            subwayStations = new ArrayList<>(subwayStationCache.get(SUBWAY_STATION_KEY).values());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return subwayStations;
    }

    private ExtensionRegistry getGtfsExtensionRegistry() {
        if (gtfsExtensionRegistry == null) {
            gtfsExtensionRegistry = ExtensionRegistry.newInstance();
            gtfsExtensionRegistry.add(GtfsRealtimeNYCT.nyctFeedHeader);
            gtfsExtensionRegistry.add(GtfsRealtimeNYCT.nyctStopTimeUpdate);
            gtfsExtensionRegistry.add(GtfsRealtimeNYCT.nyctTripDescriptor);
        }
        return gtfsExtensionRegistry;
    }

    private URL buildFeedUrl(int feedId) {
        try {
            return new URL(String.format("%s?feed_id=%s&key=%s", MTA_SUBWAY_FEED_BASE_URL, feedId, MTA_SUBWAY_FEED_API_KEY));
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Unable to generate URL for feed id " + feedId);
        }
    }

    private List<SubwayStation> fetchSubwayStations() throws IOException {
        InputStream inputStream = null;
        //try to load the data from the server; if not available, get from disk
        try {
            URL url = new URL(MTA_SUBWAY_STATIONS_URL);
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("User-Agent", TransitConstants.USER_AGENT_STRING);
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            inputStream = MTASubwayGTFSDaoImpl.class.getResourceAsStream("/metadata/Stations.csv");
        }
        return CSVParser.parse(inputStream, Charset.defaultCharset(),  CSVFormat.EXCEL.withFirstRecordAsHeader())
                .getRecords()
                .stream()
                .map(r -> new SubwayStation(
                    r.get(STATION_ID),
                    r.get(GTFS_STOP_ID),
                    r.get(STOP_NAME),
                    r.get(BOROUGH),
                    Arrays.stream(r.get(DAYTIME_ROUTES).split(" ")).collect(Collectors.toList()),
                    Double.parseDouble(r.get(GTFS_LATITUDE)),
                    Double.parseDouble(r.get(GTFS_LONGITUDE))
                )).collect(Collectors.toList());
    }

    @Override
    public SubwayStatusResponse getSubwayStatus() {
        try {
            SubwayStatusResponse statusResponse = new SubwayStatusResponse();
            List<SubwayStatusResponse.SubwayStatus> subwayStatusList = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            URL url = new URL(MTA_SUBWAY_STATUS_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", TransitConstants.USER_AGENT_STRING);
            Document subwayStatusDoc = builder.parse(connection.getInputStream());

            NodeList responseTimestampNodes = subwayStatusDoc.getDocumentElement().getElementsByTagName("ResponseTimestamp");
            for (int i = 0; i < responseTimestampNodes.getLength(); i++) {
                Node responseTimeNode = responseTimestampNodes.item(i);
                if ("ResponseTimestamp".equals(responseTimeNode.getNodeName())) {
                    statusResponse.setLastUpdated(responseTimeNode.getTextContent());
                    break;
                }
            }

            NodeList situationNodeList = subwayStatusDoc.getDocumentElement().getElementsByTagName("PtSituationElement");
            for (int i = 0; i < situationNodeList.getLength(); i++) {
                Node situationNode = situationNodeList.item(i);
                subwayStatusList.add(mapSituationNodeToSubwayStatus.apply(situationNode));
            }
            statusResponse.setSubwayStatusList(subwayStatusList);

            return statusResponse;
        } catch (Exception e) {
           throw new RuntimeException("Unable to fetch Subway Status", e);
        }
    }
}
