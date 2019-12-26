package com.transit.domain.mta;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 *  Provides filtering criteria to search for or discover subway information. Some possibilities:
 *      a) Find a trip by id
 *      b) Find all trips by route(i.e. F)
 *      c) Find all trips going through specific station
 *      d) Find all trips for a given latitude/longitude
 *      e) Find all trips arriving at specific station within given time frame
 *      f) Find all trips by direction
 *
 *  These filter can be combined to produce more granular results
 *
 *  Optimized order of filtration. We will execute the filters based on how uniquely identifying each filter is:
 *      1. Check for route filter first to isolate the feeds. Route is a special filter because we use it fetch a list of trips.
 *         All the other filters are applied *AFTER* the trips are fetched so we need to use route differently.
 *         If not present, we have to fetch all the feeds which is expensive.
 *
 *      2. All the other filter's can be combined into a "complex" predicate which will run on all fetched records
 *
 *          For the lat/lng filter, we will create a n-mile radius around the supplied lat-lng and see if the station
 *          falls within the radius. This is not highly accurate because it doesn't take into account elevation, accessibility, etc.
 *
 *          arrivingIn filter is only applicable if a station is specified
 *
 *
 * */
public class SubwayTripListParams {
    private List<String> routes;
    private List<String> tripIds;
    private List<String> gtfsStopIds;
    private double latitude = Double.MIN_VALUE;
    private double longitude = Double.MIN_VALUE;
    private double searchRadius = 1; //miles
    private Trip.Direction direction = null;
    private long arrivingIn = Long.MIN_VALUE; //milliseconds

    public static final class Builder {
        private List<String> routes;
        private List<String> tripIds;
        private List<String> gtfsStopIds;
        private double latitude = Double.MIN_VALUE;
        private double longitude = Double.MIN_VALUE;
        private double searchRadius = 1; //miles
        private Trip.Direction direction = null;
        private long arrivingIn = Long.MIN_VALUE; //milliseconds

        public Builder withTripIds(List<String> tripIds) {
            this.tripIds = tripIds;
            return this;
        }

        public Builder withRoutes(List<String> routes) {
            this.routes = routes;
            return this;
        }

        public Builder withGtfsStopIds(List<String> gtfsStopIds) {
            this.gtfsStopIds = gtfsStopIds;
            return this;
        }

        public Builder withLatitudeLongitude(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            return this;
        }

        /**
         *  Search radius expressed in miles. This field is only valid if a latitude and longitude
         *  are specified
         * */
        public Builder withSearchRadius(double radius) {
            this.searchRadius = radius;
            return this;
        }

        public Builder withDirection(Trip.Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder withArrivingIn(long arrivingIn) {
            this.arrivingIn = arrivingIn;
            return this;
        }

        public SubwayTripListParams build() {
            SubwayTripListParams subwayTripListParams = new SubwayTripListParams();
            subwayTripListParams.longitude = this.longitude;
            subwayTripListParams.direction = this.direction;
            subwayTripListParams.tripIds = this.tripIds;
            subwayTripListParams.gtfsStopIds = this.gtfsStopIds;
            subwayTripListParams.latitude = this.latitude;
            subwayTripListParams.routes = this.routes;
            subwayTripListParams.arrivingIn = this.arrivingIn;
            subwayTripListParams.searchRadius = this.searchRadius;
            return subwayTripListParams;
        }
    }

    public boolean isTripIdSearch() {
        return !CollectionUtils.isEmpty(tripIds);
    }

    public boolean isRouteSearch() {
        return !CollectionUtils.isEmpty(routes);
    }

    public boolean includesStopIds() {
        return !CollectionUtils.isEmpty(gtfsStopIds);
    }

    public boolean isLocationSearch() {
        return latitude != Double.MIN_VALUE && longitude != Double.MIN_VALUE;
    }

    public boolean includesHeading() {
        return direction != null;
    }

    public boolean isTimeSearch() {
        return arrivingIn != Long.MIN_VALUE;
    }

    public List<String> getTripIds() {
        return tripIds;
    }

    public List<String> getRoutes() {
        return routes;
    }

    public List<String> getGtfsStopIds() {
        return gtfsStopIds;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Trip.Direction getDirection() {
        return direction;
    }

    public long getArrivingIn() {
        return arrivingIn;
    }

    public double getSearchRadius() {
        return searchRadius;
    }
}
