package com.transit.dao.impl;

import com.transit.dao.MTABusServiceDao;
import com.transit.domain.mta.bus.*;
import com.transit.domain.mta.subway.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MTABusServiceDaoImpl implements MTABusServiceDao {

    @Value("${mta.api.bus.key}")
    private String MTA_BUS_KEY;

    @Value("${mta.api.bus.bustime-base-url}")
    private String MTA_BUSTIME_BASE_URL;

    @Value("${mta.api.bus.siri-vehicle-url}")
    private String SIRI_VEHICLE_URL;

    @Value("${mta.api.bus.siri-stop-url}")
    private String SIRI_STOP_URL;

    private final String MTA_AGENCY = "MTA NYCT";
    private final String ROUTES_FOR_AGENCY_PATH = String.format("/routes-for-agency/%s.json", MTA_AGENCY);
    private final String ROUTES_FOR_LOCATION_PATH = "/routes-for-location.json";
    private final String STOPS_FOR_ROUTE_PATH_TEMPLATE = "/stops-for-route/%s.json"; //placeholder for route_id
    private final String STOPS_FOR_LOCATION_PATH = "/stops-for-location.json";

    @Autowired
    private RestOperations restOperations;

    @Override
    public List<Route> listRoutes(BusListParams busListParams) {
        StringBuilder url = new StringBuilder(MTA_BUSTIME_BASE_URL);
        LinkedMultiValueMap<String, String> urlParamMap = new LinkedMultiValueMap<>();
        urlParamMap.add("key", MTA_BUS_KEY);

        if (busListParams.isLocationSearch()) {
            //http://bustime.mta.info/api/where/routes-for-location.json?key=db618ed9-fa13-4953-8e88-e4c0d0fdf16e&lat=40.702659&lon=-73.821620&radius=10000&query=q56
            url.append(ROUTES_FOR_LOCATION_PATH);
            urlParamMap.add("lat", Double.toString(busListParams.getLatitude()));
            urlParamMap.add("lon", Double.toString(busListParams.getLongitude()));
            urlParamMap.add("radius", Double.toString(busListParams.getSearchRadius()));
            //query only works if a position is specified
            if (busListParams.isQuerySearch()) {
                urlParamMap.add("query", busListParams.getQuery());
            }
        } else {
            //http://bustime.mta.info/api/where/routes-for-agency/MTA%20NYCT.json?key=db618ed9-fa13-4953-8e88-e4c0d0fdf16e
            url.append(ROUTES_FOR_AGENCY_PATH);
        }

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url.toString()).queryParams(urlParamMap).build();
        ResponseEntity<MetaDataResponse<MetaDataResponse.ListResponse<Route>>> response = restOperations.exchange(builder.toString(),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<MetaDataResponse<MetaDataResponse.ListResponse<Route>>>(){});
        return response.getBody().getData().getList();
    }

    @Override
    public StopResponse.Entry listStops(BusListParams busListParams) {
        StopResponse.Entry responseEntry = null;
        StringBuilder url = new StringBuilder(MTA_BUSTIME_BASE_URL);
        LinkedMultiValueMap<String, String> urlParamMap = new LinkedMultiValueMap<>();
        urlParamMap.add("key", MTA_BUS_KEY);

        if (busListParams.isLocationSearch()) {
            //http://bustime.mta.info/api/where/stops-for-location.json?lat=40.702659&lon=-73.821620&radius=1000&key=db618ed9-fa13-4953-8e88-e4c0d0fdf16e&query=503084
            url.append(STOPS_FOR_LOCATION_PATH);
            urlParamMap.add("lat", Double.toString(busListParams.getLatitude()));
            urlParamMap.add("lon", Double.toString(busListParams.getLongitude()));
            urlParamMap.add("radius", Double.toString(busListParams.getSearchRadius()));
            //query only works if a position is specified
            if (busListParams.isQuerySearch()) {
                urlParamMap.add("query", busListParams.getQuery());
            }
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url.toString()).queryParams(urlParamMap).build();
            ResponseEntity<MetaDataResponse<MetaDataResponse.ListResponse<Stop>>> response = restOperations.exchange(builder.toString(),
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    new ParameterizedTypeReference<MetaDataResponse<MetaDataResponse.ListResponse<Stop>>>(){});
            List<Stop> stopList = response.getBody().getData().getList();

            StopResponse.Group stopGroup = new StopResponse.Group();
            stopGroup.setStops(stopList);
            StopResponse.StopGrouping stopGrouping = new StopResponse.StopGrouping();
            stopGrouping.setStopGroups(Arrays.asList(stopGroup));
            responseEntry = new StopResponse.Entry();
            responseEntry.setStopGroupings(Arrays.asList(stopGrouping));

        } else {
            //http://bustime.mta.info/api/where/stops-for-route/MTA%20NYCT_Q54.json?key=db618ed9-fa13-4953-8e88-e4c0d0fdf16e&includePolylines=false&version=2
            urlParamMap.add("version", "2"); //use the version 2 of API
            url.append(String.format(STOPS_FOR_ROUTE_PATH_TEMPLATE, busListParams.getRouteId()));
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url.toString()).queryParams(urlParamMap).build();
            ResponseEntity<MetaDataResponse<StopResponse>> response = restOperations.exchange(builder.toString(),
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    new ParameterizedTypeReference<MetaDataResponse<StopResponse>>(){});
            //generate a list of stops enriched with the actual routes in the stops themselves
            StopResponse stopResponse = response.getBody().getData();
            StopResponse.Entry entry = stopResponse.getEntry();
            StopResponse.References references = stopResponse.getReferences();

            Map<String, Route> routeIdMap = references.getRoutes().stream().collect(Collectors.toMap(Route::getId, Function.identity()));
            Map<String, Stop> tripIdMap = references.getStops().stream().collect(Collectors.toMap(Stop::getId, stop -> stop.setRoutes(stop.getRouteIds().stream().map(routeIdMap::get).collect(Collectors.toList()))));
            entry.getStopGroupings()
                    .parallelStream()
                    .flatMap(stopGrouping -> stopGrouping.getStopGroups().stream())
                    .forEach(group -> group.setStops(group.getStopIds()
                            .parallelStream()
                            .map(tripIdMap::get)
                            .collect(Collectors.toList())));
            responseEntry = entry;
        }
        return responseEntry;
    }

    @Override
    public SiriResponse listTrips(BusListParams busListParams) {
        StringBuilder url = null;
        LinkedMultiValueMap<String, String> urlParamMap = new LinkedMultiValueMap<>();
        urlParamMap.add("key", MTA_BUS_KEY);
        urlParamMap.add("version", "2");
        urlParamMap.add("OperatorRef", "MTA");

        if (busListParams.isStopSearch()) {
            url = new StringBuilder(SIRI_STOP_URL);
            urlParamMap.add("MonitoringRef", busListParams.getStopId());
            if (busListParams.isRouteIdSearch()) {
                urlParamMap.add("LineRef", busListParams.getRouteId());
            }
            if (busListParams.isDirectionSearch()) {
                urlParamMap.add("DirectionRef", busListParams.getDirection() == Trip.Direction.NORTH ? "1" : "0");
            }
        } else {
            url = new StringBuilder(SIRI_VEHICLE_URL);
            if (busListParams.isRouteIdSearch()) {
                urlParamMap.add("LineRef", busListParams.getRouteId());
            }
            if (busListParams.isVehicleSearch()) {
                urlParamMap.add("VehicleRef", busListParams.getVehicleId());
            }
            if (busListParams.isDirectionSearch()) {
                urlParamMap.add("DirectionRef", busListParams.getDirection() == Trip.Direction.NORTH ? "0" : "1");
            }
        }
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url.toString()).queryParams(urlParamMap).build();
        ResponseEntity<SiriResponse> response = restOperations.getForEntity(builder.toString(), SiriResponse.class);
        return response.getBody();
    }
}
