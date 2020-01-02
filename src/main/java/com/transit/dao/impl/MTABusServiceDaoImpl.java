package com.transit.dao.impl;

import com.transit.dao.MTABusServiceDao;
import com.transit.domain.mta.bus.BusListParams;
import com.transit.domain.mta.bus.MetaDataResponse;
import com.transit.domain.mta.bus.Route;
import com.transit.domain.mta.bus.Stop;
import org.apache.commons.collections4.map.MultiValueMap;
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

import java.util.List;

@Component
public class MTABusServiceDaoImpl implements MTABusServiceDao {

    @Value("${mta.api.bus.key}")
    private String MTA_BUS_KEY;

    @Value("${mta.api.bus.bustime-base-url}")
    private String MTA_BUSTIME_BASE_URL;

    @Value("${mta.api.bus.bus-tracking-url}")
    private String MTA_BUS_TRACKING_URL;

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
        ResponseEntity<MetaDataResponse<MetaDataResponse.RouteResponse>> response = restOperations.exchange(builder.toString(),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<MetaDataResponse<MetaDataResponse.RouteResponse>>(){});
        return response.getBody().getData().getList();
    }

    @Override
    public List<Stop> listStops(BusListParams busListParams) {
        return null;
    }
}
