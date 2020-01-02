package com.transit.domain.mta.bus;

import org.apache.commons.lang3.StringUtils;

public class BusListParams {
    private double latitude = Double.MIN_VALUE;
    private double longitude = Double.MIN_VALUE;
    private double searchRadius = 1; //in miles - actual API is in meters so conversion is needed
    private String query;

    private BusListParams() {}

    public static final class Builder {
        private double latitude = Double.MIN_VALUE;
        private double longitude = Double.MIN_VALUE;
        private double searchRadius = 1; //in miles - actual API is in meters so conversion is needed
        private String query;

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

        public BusListParams build() {
            BusListParams busListParams = new BusListParams();
            busListParams.searchRadius = this.searchRadius;
            busListParams.latitude = this.latitude;
            busListParams.longitude = this.longitude;
            busListParams.query = this.query;
            return busListParams;
        }
    }

    public boolean isLocationSearch() {
        return latitude != Double.MIN_VALUE && longitude != Double.MIN_VALUE && searchRadius > 0;
    }

    public boolean isQuerySearch() {
        return StringUtils.isNotEmpty(query);
    }

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

    public String getQuery() {
        return query;
    }

    public BusListParams setQuery(String query) {
        this.query = query;
        return this;
    }
}
