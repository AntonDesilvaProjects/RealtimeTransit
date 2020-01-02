package com.transit.domain.mta.subway;

import java.util.List;

public class SubwayStation {
    private String stationId;
    private String gtfsStopId;
    private String name;
    private String borough;
    private List<String> dayTimeRoutes;
    private double gtfsLatitude;
    private double getGtfsLongitude;

    public SubwayStation(String stationId, String gtfsStopId, String name, String borough, List<String> dayTimeRoutes, double gtfsLatitude, double getGtfsLongitude) {
        this.stationId = stationId;
        this.gtfsStopId = gtfsStopId;
        this.name = name;
        this.borough = borough;
        this.dayTimeRoutes = dayTimeRoutes;
        this.gtfsLatitude = gtfsLatitude;
        this.getGtfsLongitude = getGtfsLongitude;
    }

    public String getStationId() {
        return stationId;
    }

    public SubwayStation setStationId(String stationId) {
        this.stationId = stationId;
        return this;
    }

    public String getGtfsStopId() {
        return gtfsStopId;
    }

    public SubwayStation setGtfsStopId(String gtfsStopId) {
        this.gtfsStopId = gtfsStopId;
        return this;
    }

    public String getName() {
        return name;
    }

    public SubwayStation setName(String name) {
        this.name = name;
        return this;
    }

    public String getBorough() {
        return borough;
    }

    public SubwayStation setBorough(String borough) {
        this.borough = borough;
        return this;
    }

    public double getGtfsLatitude() {
        return gtfsLatitude;
    }

    public SubwayStation setGtfsLatitude(double gtfsLatitude) {
        this.gtfsLatitude = gtfsLatitude;
        return this;
    }

    public double getGetGtfsLongitude() {
        return getGtfsLongitude;
    }

    public SubwayStation setGetGtfsLongitude(double getGtfsLongitude) {
        this.getGtfsLongitude = getGtfsLongitude;
        return this;
    }

    public List<String> getDayTimeRoutes() {
        return dayTimeRoutes;
    }

    public SubwayStation setDayTimeRoutes(List<String> dayTimeRoutes) {
        this.dayTimeRoutes = dayTimeRoutes;
        return this;
    }
}
