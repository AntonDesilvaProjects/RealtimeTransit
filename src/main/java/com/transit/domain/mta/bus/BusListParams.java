package com.transit.domain.mta.bus;

import com.transit.domain.mta.subway.Trip;
import org.apache.commons.lang3.StringUtils;

public class BusListParams {
    private double latitude = Double.MIN_VALUE;
    private double longitude = Double.MIN_VALUE;
    private double searchRadius = 1; //in miles - actual API is in meters so conversion is needed
    private String query;
    private String routeId;
    private String stopId;
    private String vehicleId;
    private Trip.Direction direction;

    private BusListParams() {}

    public static final class Builder {
        private double latitude = Double.MIN_VALUE;
        private double longitude = Double.MIN_VALUE;
        private double searchRadius = 1; //in miles - actual API is in meters so conversion is needed
        private String query;
        private String routeId;
        private String stopId;
        private String vehicleId;
        private Trip.Direction direction;

        public Builder() {
        }

        public Builder withLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder withLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder withSearchRadius(double searchRadius) {
            this.searchRadius = searchRadius;
            return this;
        }

        public Builder withQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder withRouteId(String routeId) {
            this.routeId = routeId;
            return this;
        }

        public Builder withStopId(String stopId) {
            this.stopId = stopId;
            return this;
        }

        public Builder withDirection(Trip.Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder withVehicleId(String vehicleId) {
            this.vehicleId = vehicleId;
            return this;
        }

        public BusListParams build() {
            BusListParams busListParams = new BusListParams();
            busListParams.searchRadius = this.searchRadius;
            busListParams.latitude = this.latitude;
            busListParams.longitude = this.longitude;
            busListParams.query = this.query;
            busListParams.routeId = this.routeId;
            busListParams.stopId = this.stopId;
            busListParams.vehicleId = this.vehicleId;
            busListParams.direction = this.direction;
            return busListParams;
        }
    }

    public boolean isLocationSearch() {
        return latitude != Double.MIN_VALUE && longitude != Double.MIN_VALUE && searchRadius > 0;
    }

    public boolean isQuerySearch() {
        return StringUtils.isNotEmpty(query);
    }

    public boolean isRouteIdSearch() { return StringUtils.isNotEmpty(routeId); }

    public boolean isStopSearch() { return StringUtils.isNotEmpty(stopId); }

    public boolean isDirectionSearch() { return direction != null; }

    public boolean isVehicleSearch() { return StringUtils.isNotEmpty(vehicleId); }

    public double getLatitude() {
        return latitude;
    }

    public BusListParams setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public BusListParams setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getSearchRadius() {
        return searchRadius;
    }

    public BusListParams setSearchRadius(double searchRadius) {
        this.searchRadius = searchRadius;
        return this;
    }

    public String getStopId() {
        return stopId;
    }

    public BusListParams setStopId(String stopId) {
        this.stopId = stopId;
        return this;
    }

    public Trip.Direction getDirection() {
        return direction;
    }

    public BusListParams setDirection(Trip.Direction direction) {
        this.direction = direction;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public BusListParams setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getRouteId() {
        return routeId;
    }

    public BusListParams setRouteId(String routeId) {
        this.routeId = routeId;
        return this;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public BusListParams setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
        return this;
    }
}
