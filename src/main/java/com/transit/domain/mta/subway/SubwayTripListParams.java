package com.transit.domain.mta.subway;

import com.transit.utils.GeoUtils;
import org.apache.commons.collections4.ComparatorUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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
    private Map<Sorting.Field, Sorting.Order> sortMap;

    private final Comparator<Trip> DISTANCE_SORTER = (trip1, trip2) -> {
        //find the TripUpdate with the smallest distance for each trip
        double trip1SmallestDistance = trip1.getTripUpdates().stream().filter(TripUpdate::isMatchedSearch)
                .map(tripUpdate -> GeoUtils.distance(
                        tripUpdate.getSubwayStation().getGtfsLatitude(),
                        this.getLatitude(),
                        tripUpdate.getSubwayStation().getGetGtfsLongitude(),
                        this.getLongitude())
                ).min(Double::compare).orElse(Double.MAX_VALUE);
        double trip2SmallestDistance = trip2.getTripUpdates().stream().filter(TripUpdate::isMatchedSearch)
                .map(tripUpdate -> GeoUtils.distance(
                        tripUpdate.getSubwayStation().getGtfsLatitude(),
                        this.getLatitude(),
                        tripUpdate.getSubwayStation().getGetGtfsLongitude(),
                        this.getLongitude())
                ).min(Double::compare).orElse(Double.MAX_VALUE);

        return Double.compare(trip1SmallestDistance, trip2SmallestDistance);
    };

    private final Comparator<Trip> TIME_SORTER = (trip1, trip2) -> {
        //find the TripUpdate with the shortest time for each trip
        long trip1ShortestTime = trip1.getTripUpdates().stream().filter(TripUpdate::isMatchedSearch).map(TripUpdate::getArrivingIn).min(Long::compare).orElse(Long.MAX_VALUE);
        long trip2ShortestTime =trip2.getTripUpdates().stream().filter(TripUpdate::isMatchedSearch).map(TripUpdate::getArrivingIn).min(Long::compare).orElse(Long.MAX_VALUE);
        return Long.compare(trip1ShortestTime, trip2ShortestTime);
    };
    
    public static final class Builder {
        private List<String> routes;
        private List<String> tripIds;
        private List<String> gtfsStopIds;
        private double latitude = Double.MIN_VALUE;
        private double longitude = Double.MIN_VALUE;
        private double searchRadius = 1; //miles
        private Trip.Direction direction = null;
        private long arrivingIn = Long.MIN_VALUE; //milliseconds
        private Map<Sorting.Field, Sorting.Order> sortMap = new LinkedHashMap<>(); //preserve the insertion order and apply to sort

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

        public Builder sortedBy(Sorting.Field field, Sorting.Order order) {
           this.sortMap.put(field, order);
           return this;
        }

        public Builder sortedByFromString(String sortText) {
            if (!StringUtils.isEmpty(sortText)) {
                String[] sorts = sortText.split(",");
                for (String sort : sorts) {
                    String[] sortFieldOrder = sort.split(":");
                    if (sortFieldOrder.length != 2) {
                        throw new IllegalArgumentException(sortText + " is an invalid sort text!");
                    }
                    this.sortMap.put(Sorting.Field.fromString(sortFieldOrder[0]), Sorting.Order.fromString(sortFieldOrder[1]));
                }
            }
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
            subwayTripListParams.sortMap = Collections.unmodifiableMap(this.sortMap);
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

    public Map<Sorting.Field, Sorting.Order> getSortMap() {
        return sortMap;
    }

    public Optional<Comparator<Trip>> buildSorter() {
        List<Comparator<Trip>> sortList = sortMap.keySet().stream().map(f -> getSorter(f, sortMap.get(f))).filter(Objects::nonNull).collect(Collectors.toList());
        return CollectionUtils.isEmpty(sortList) ? Optional.empty() : Optional.of(ComparatorUtils.chainedComparator(sortList));
    }

    private Comparator<Trip> getSorter(Sorting.Field field, Sorting.Order order) {
        Comparator<Trip> sorter = null;
        switch (field) {
            case ROUTE:
                sorter = Comparator.comparing(Trip::getRouteId);
                break;
            case DISTANCE:
                sorter = isLocationSearch() ? DISTANCE_SORTER : null;
                break;
            case ARRIVAL_TIME:
                sorter = TIME_SORTER;
                break;
            default:
                return null;
        }
        if (sorter != null && order == Sorting.Order.DESCENDING) {
            sorter = sorter.reversed();
        }
        return sorter;
    }
}
