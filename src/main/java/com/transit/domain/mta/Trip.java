package com.transit.domain.mta;

import java.util.Arrays;
import java.util.List;

public class Trip {

    private String tripId;
    private String routeId;
    private String startTime;
    private String startDate;
    private long startDateTime;
    private String trainId;
    private Direction direction;
    private List<TripUpdate> tripUpdates;

    public enum Direction {
        NORTH, SOUTH;
        public static Direction fromString(String direction) {
           return Arrays.stream(Direction.values())
                   .filter(d -> d.name().equalsIgnoreCase(direction))
                   .findFirst()
                   .orElseThrow(() -> new IllegalArgumentException("Invalid direction string!"));
        }
    }

    public static class builder {
        private String tripId;
        private String routeId;
        private String startTime;
        private String startDate;
        private long startDateTime;
        private String trainId;
        private Direction direction;
        private List<TripUpdate> tripUpdates;

        public builder(String tripId) {
            this.tripId = tripId;
        }
        public builder withRouteId(String routeId) {
            this.routeId = routeId;
            return this;
        }
        public builder startingAt(String startTime) {
            this.startTime = startTime;
            return this;
        }
        public builder startingOn(String startingDate) {
            this.startDate = startingDate;
            return this;
        }
        public builder on(long startDateTime) {
            this.startDateTime = startDateTime;
            return this;
        }
        public builder withTrainId(String trainId) {
            this.trainId = trainId;
            return this;
        }
        public builder headed(Direction direction) {
            this.direction = direction;
            return this;
        }
        public builder havingTripUpdates(List<TripUpdate> tripUpdates) {
            this.tripUpdates = tripUpdates;
            return this;
        }

        public Trip build() {
            Trip trip = new Trip();
            trip.setTripId(this.tripId);
            trip.setRouteId(this.routeId);
            trip.setStartDate(this.startDate);
            trip.setStartTime(this.startTime);
            trip.setStartDateTime(this.startDateTime);
            trip.setTripId(this.trainId);
            trip.setDirection(this.direction);
            trip.setTripUpdates(this.tripUpdates);
            return trip;
        }
    }

    private Trip() {}

    public String getTripId() {
        return tripId;
    }

    public Trip setTripId(String tripId) {
        this.tripId = tripId;
        return this;
    }

    public String getRouteId() {
        return routeId;
    }

    public Trip setRouteId(String routeId) {
        this.routeId = routeId;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public Trip setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getStartDate() {
        return startDate;
    }

    public Trip setStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public long getStartDateTime() {
        return startDateTime;
    }

    public Trip setStartDateTime(long startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public String getTrainId() {
        return trainId;
    }

    public Trip setTrainId(String trainId) {
        this.trainId = trainId;
        return this;
    }

    public Direction getDirection() {
        return direction;
    }

    public Trip setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public List<TripUpdate> getTripUpdates() {
        return tripUpdates;
    }

    public Trip setTripUpdates(List<TripUpdate> tripUpdates) {
        this.tripUpdates = tripUpdates;
        return this;
    }
}
