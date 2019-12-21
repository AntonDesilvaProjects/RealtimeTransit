package com.transit.dao.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.protobuf.ExtensionRegistry;
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtimeNYCT;
import com.transit.TransitConstants;
import com.transit.dao.MTASubwayGTFSDao;
import com.transit.domain.mta.Feed;
import com.transit.domain.mta.SubwayStation;
import com.transit.domain.mta.Trip;
import com.transit.domain.mta.TripUpdate;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MTASubwayGTFSDaoImpl implements MTASubwayGTFSDao {

    @Value("${mta.api.feed-base-url}")
    private String MTA_SUBWAY_FEED_BASE_URL;

    @Value("${mta.api.stations-url}")
    private String MTA_SUBWAY_STATIONS_URL;

    @Value("${mta.api.key}")
    private String MTA_SUBWAY_FEED_API_KEY;

    private ExtensionRegistry gtfsExtensionRegistry;
    private LoadingCache<Integer, SubwayStation> subwayStationCache;

    @PostConstruct
    public void init() {
//        subwayStationCache = CacheBuilder.newBuilder()
//                .expireAfterAccess(1, TimeUnit.DAYS)
//                .build(new CacheLoader<Integer, SubwayStation>() {
//                        @Override
//                        public SubwayStation load(Integer integer) throws Exception {
//                            return fetchSubwayStations().get(0);
//                        }
//                    });
//        List<Trip> feeds = getTripsForFeed(26);

        try {
            fetchSubwayStations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Feed> getAvailableFeeds() {
        //there is no dynamic way to pull this data so return a pre-built list
        return (List<Feed>) TransitConstants.FEED_ID_MAP.values();
    }

    @Override
    public List<Trip> getTripsForFeed(int feedId) {
        List<Trip> trips = new ArrayList<>();
        try (InputStream urlStream = buildFeedUrl(feedId).openStream()) {
            GtfsRealtime.FeedMessage feedMessage = GtfsRealtime.FeedMessage.parseFrom(urlStream, getGtfsExtensionRegistry());
            for (GtfsRealtime.FeedEntity entity : feedMessage.getEntityList()) {
                if (entity.hasTripUpdate()) {
                    GtfsRealtime.TripUpdate gtfsTripUpdate = entity.getTripUpdate();
                    List<GtfsRealtime.TripUpdate.StopTimeUpdate> updateList = gtfsTripUpdate.getStopTimeUpdateList();
                    List<TripUpdate> tripUpdates = updateList.stream().map(u ->
                            new TripUpdate.builder()
                                .withStopId(u.getStopId())
                                .arrivingOn(u.getArrival().getTime())
                                .departingOn(u.getDeparture().getTime()).build()
                    ).collect(Collectors.toList());

                    GtfsRealtime.TripDescriptor tripDescriptor = gtfsTripUpdate.getTrip();
                    Trip trip = new Trip.builder(tripDescriptor.getTripId())
                            .withRouteId(tripDescriptor.getRouteId())
                            .startingAt(tripDescriptor.getStartTime())
                            .startingOn(tripDescriptor.getStartDate())
                            .withTrainId(tripDescriptor.getTripId())
                            .havingTripUpdates(tripUpdates)
                            .build();
                    trips.add(trip);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trips;
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
        List<SubwayStation> subwayStations = null;
        List<CSVRecord> csvRecords = CSVParser.parse(new URL(MTA_SUBWAY_STATIONS_URL), Charset.defaultCharset(),  CSVFormat.EXCEL.withFirstRecordAsHeader()).getRecords();
        System.out.println(csvRecords.get(10).get("GTFS Stop ID"));
        return subwayStations;
    }
}
