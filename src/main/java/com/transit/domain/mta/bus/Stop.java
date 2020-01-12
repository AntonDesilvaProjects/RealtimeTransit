package com.transit.domain.mta.bus;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

public class Stop {
    private String code;
    private String name;
    private String direction;
    private String id;
    @JsonAlias("lat")
    private double latitude;
    @JsonAlias("lon")
    private double longitude;
    private String wheelchairBoarding;
    private List<Route> routes;
    private List<String> routeIds;

    public String getCode() {
        return code;
    }

    public Stop setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public Stop setName(String name) {
        this.name = name;
        return this;
    }

    public String getDirection() {
        return direction;
    }

    public Stop setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public String getId() {
        return id;
    }

    public Stop setId(String id) {
        this.id = id;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public Stop setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public Stop setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getWheelChairBoarding() {
        return wheelchairBoarding;
    }

    public Stop setWheelChairBoarding(String wheelChairBoarding) {
        this.wheelchairBoarding = wheelChairBoarding;
        return this;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public Stop setRoutes(List<Route> routes) {
        this.routes = routes;
        return this;
    }

    public List<String> getRouteIds() {
        return routeIds;
    }

    public Stop setRouteIds(List<String> routeIds) {
        this.routeIds = routeIds;
        return this;
    }
}
