package com.transit.domain.mta.subway;

import com.google.transit.realtime.GtfsRealtimeNYCT;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private String destination;

    public enum Direction {
        NORTH, SOUTH, EAST, WEST;
        public static Direction fromString(String direction) {
           return Arrays.stream(Direction.values())
                   .filter(d -> d.name().equalsIgnoreCase(direction) ||
                           (StringUtils.isNotEmpty(direction) && direction.length() == 1 && StringUtils.startsWithIgnoreCase(d.name(), direction)))
                   .findFirst()
                   .orElseThrow(() -> new IllegalArgumentException("Invalid direction string!"));
        }
        public static Direction fromMTADirection(GtfsRealtimeNYCT.NyctTripDescriptor.Direction direction) {
            switch (direction) {
                case NORTH:
                    return Direction.NORTH;
                case SOUTH:
                    return Direction.SOUTH;
                case EAST:
                    return Direction.EAST;
                case WEST:
                    return Direction.WEST;
            }
            throw new IllegalArgumentException(direction + " is not supported!");
        }
    }

    public static class builder {
        private String tripId;
        private String routeId;
        private String startTime;
        private String startDate;
        private long startDateTime = -1L;
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
            trip.setTrainId(this.trainId);
            trip.setDirection(this.direction);
            trip.setTripUpdates(this.tripUpdates);

            if(this.startDateTime == -1L && StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(startTime)) {
                trip.setStartDateTime(LocalDateTime.parse(startDate + " " + startTime,
                        DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")).atZone(ZoneId.of("America/New_York")).toInstant().toEpochMilli());
            }
            //set the last stop of the train
            //if there are no trip updates, then the train is already in the final destination?
            if (!CollectionUtils.isEmpty(this.tripUpdates)) {
                trip.setDestination(this.tripUpdates.get(this.tripUpdates.size() - 1).getSubwayStation().getName());
            }
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

    public String getDestination() {
        return destination;
    }

    public Trip setDestination(String destination) {
        this.destination = destination;
        return this;
    }
}
