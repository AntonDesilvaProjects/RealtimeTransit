package com.transit.domain.mta.bus;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class MonitoredVehicleJourney {

    @JsonAlias("LineRef")
    private String lineRef;
    @JsonAlias("DirectionRef")
    private String directionRef;
    @JsonAlias("PublishedLineName")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> publishedLineName;
    @JsonAlias("DestinationName")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> destinationName; //end destination name
    @JsonAlias("OriginRef")
    private String originRef; //start bus stop
    @JsonAlias("DestinationRef")
    private String destinationRef; //end bus stop
    private double latitude;
    private double longitude;
    @JsonAlias("Bearing")
    private double bearing;
    @JsonAlias("ProgressRate")
    private String progressRate;
    @JsonAlias("VehicleRef")
    private String vehicleRef;
    @JsonAlias("MonitoredCall")
    private MonitoredCall monitoredCall;

    @JsonProperty("VehicleLocation")
    public void unwrapLatitudeLongitude(Map<String, Double> positionMap) {
        this.latitude = positionMap.get("Latitude");
        this.longitude = positionMap.get("Longitude");
    }
    public static class MonitoredCall {
        @JsonAlias("ArrivalProximityText")
        private String arrivalProximityText;
        @JsonAlias("DistanceFromStop")
        private double distanceFromStop; //in meters
        @JsonAlias("NumberOfStopsAway")
        private int numberOfStopsAway;
        @JsonAlias("StopPointRef")
        private String stopPointRef;
        @JsonAlias("VisitNumber")
        private int visitNumber;
        @JsonAlias("StopPointName")
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<String> stopPointName;

        public String getArrivalProximityText() {
            return arrivalProximityText;
        }

        public MonitoredCall setArrivalProximityText(String arrivalProximityText) {
            this.arrivalProximityText = arrivalProximityText;
            return this;
        }

        public double getDistanceFromStop() {
            return distanceFromStop;
        }

        public MonitoredCall setDistanceFromStop(double distanceFromStop) {
            this.distanceFromStop = distanceFromStop;
            return this;
        }

        public int getNumberOfStopsAway() {
            return numberOfStopsAway;
        }

        public MonitoredCall setNumberOfStopsAway(int numberOfStopsAway) {
            this.numberOfStopsAway = numberOfStopsAway;
            return this;
        }

        public String getStopPointRef() {
            return stopPointRef;
        }

        public MonitoredCall setStopPointRef(String stopPointRef) {
            this.stopPointRef = stopPointRef;
            return this;
        }

        public int getVisitNumber() {
            return visitNumber;
        }

        public MonitoredCall setVisitNumber(int visitNumber) {
            this.visitNumber = visitNumber;
            return this;
        }

        public List<String> getStopPointName() {
            return stopPointName;
        }

        public MonitoredCall setStopPointName(List<String> stopPointName) {
            this.stopPointName = stopPointName;
            return this;
        }
    }

    public String getLineRef() {
        return lineRef;
    }

    public MonitoredVehicleJourney setLineRef(String lineRef) {
        this.lineRef = lineRef;
        return this;
    }

    public String getDirectionRef() {
        return directionRef;
    }

    public MonitoredVehicleJourney setDirectionRef(String directionRef) {
        this.directionRef = directionRef;
        return this;
    }

    public String getOriginRef() {
        return originRef;
    }

    public MonitoredVehicleJourney setOriginRef(String originRef) {
        this.originRef = originRef;
        return this;
    }

    public String getDestinationRef() {
        return destinationRef;
    }

    public MonitoredVehicleJourney setDestinationRef(String destinationRef) {
        this.destinationRef = destinationRef;
        return this;
    }

    public List<String> getDestinationName() {
        return destinationName;
    }

    public MonitoredVehicleJourney setDestinationName(List<String> destinationName) {
        this.destinationName = destinationName;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public MonitoredVehicleJourney setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public MonitoredVehicleJourney setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getBearing() {
        return bearing;
    }

    public MonitoredVehicleJourney setBearing(double bearing) {
        this.bearing = bearing;
        return this;
    }

    public String getProgressRate() {
        return progressRate;
    }

    public MonitoredVehicleJourney setProgressRate(String progressRate) {
        this.progressRate = progressRate;
        return this;
    }

    public String getVehicleRef() {
        return vehicleRef;
    }

    public MonitoredVehicleJourney setVehicleRef(String vehicleRef) {
        this.vehicleRef = vehicleRef;
        return this;
    }

    public MonitoredCall getMonitoredCall() {
        return monitoredCall;
    }

    public MonitoredVehicleJourney setMonitoredCall(MonitoredCall monitoredCall) {
        this.monitoredCall = monitoredCall;
        return this;
    }

    public List<String> getPublishedLineName() {
        return publishedLineName;
    }

    public MonitoredVehicleJourney setPublishedLineName(List<String> publishedLineName) {
        this.publishedLineName = publishedLineName;
        return this;
    }
}
