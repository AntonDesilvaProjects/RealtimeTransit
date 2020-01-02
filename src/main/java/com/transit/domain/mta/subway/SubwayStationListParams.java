package com.transit.domain.mta.subway;

import java.util.List;
import java.util.Map;

public class SubwayStationListParams {
    private List<String> routes;
    private List<String> gtfsStopIds;
    private List<String> boroughs;
    private String searchText;
    private double latitude = Double.MIN_VALUE;
    private double longitude = Double.MIN_VALUE;
    private double searchRadius = 1; //miles
    private Map<Sorting.Field, Sorting.Order> sortMap;
}
