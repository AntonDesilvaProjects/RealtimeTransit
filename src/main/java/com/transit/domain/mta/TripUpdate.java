package com.transit.domain.mta;

public class TripUpdate {
    private SubwayStation subwayStation;
    private long arrivalTime;
    private long departureTime;
    private String scheduledTrack;
    private String actualTrack;
    private int arrivingInMinutes;

    public static class builder {
        private SubwayStation subwayStation;
        private long arrivalTime;
        private long departureTime;
        private String scheduledTrack;
        private String actualTrack;

        public builder forSubwayStation(SubwayStation subwayStation) {
            this.subwayStation = subwayStation;
            return this;
        }
        public builder arrivingOn(long arrivingOn) {
            this.arrivalTime = arrivingOn;
            return this;
        }
        public builder departingOn(long departingOn) {
            this.departureTime = departingOn;
            return this;
        }
        public builder onScheduledTrack(String scheduledTrack) {
            this.scheduledTrack = scheduledTrack;
            return this;
        }
        public builder onActualTrack(String actualTrack) {
            this.actualTrack = actualTrack;
            return this;
        }
        public TripUpdate build() {
           TripUpdate tripUpdate = new TripUpdate();
           tripUpdate.setSubwayStation(this.subwayStation);
           tripUpdate.setArrivalTime(this.arrivalTime);
           tripUpdate.setDepartureTime(this.departureTime);
           tripUpdate.setScheduledTrack(this.scheduledTrack);
           tripUpdate.setActualTrack(this.actualTrack);
           tripUpdate.setDepartureTime(this.departureTime);
           return tripUpdate;
        }
    }

    public SubwayStation getSubwayStation() {
        return subwayStation;
    }

    public TripUpdate setSubwayStation(SubwayStation subwayStation) {
        this.subwayStation = subwayStation;
        return this;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public TripUpdate setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
        return this;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public TripUpdate setDepartureTime(long departureTime) {
        this.departureTime = departureTime;
        return this;
    }

    public String getScheduledTrack() {
        return scheduledTrack;
    }

    public TripUpdate setScheduledTrack(String scheduledTrack) {
        this.scheduledTrack = scheduledTrack;
        return this;
    }

    public String getActualTrack() {
        return actualTrack;
    }

    public TripUpdate setActualTrack(String actualTrack) {
        this.actualTrack = actualTrack;
        return this;
    }

    public int getArrivingInMinutes() {
        return arrivingInMinutes;
    }

    public TripUpdate setArrivingInMinutes(int arrivingInMinutes) {
        this.arrivingInMinutes = arrivingInMinutes;
        return this;
    }
}
