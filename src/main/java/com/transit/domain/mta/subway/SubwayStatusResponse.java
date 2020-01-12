package com.transit.domain.mta.subway;

import java.util.List;

public class SubwayStatusResponse {
    List<SubwayStatus> subwayStatusList;
    private String lastUpdated;

    public static class SubwayStatus {
        private String summary;
        private String description;
        private String longDescription;
        private boolean isPlanned;
        private String reasonName;
        private List<AffectedTrip> affectedTrips;
        private String time;

        public static class AffectedTrip {
            private String routeId;
            private Trip.Direction direction;

            public String getRouteId() {
                return routeId;
            }

            public AffectedTrip setRouteId(String routeId) {
                this.routeId = routeId;
                return this;
            }

            public Trip.Direction getDirection() {
                return direction;
            }

            public AffectedTrip setDirection(Trip.Direction direction) {
                this.direction = direction;
                return this;
            }
        }

        public String getSummary() {
            return summary;
        }

        public SubwayStatus setSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public SubwayStatus setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getLongDescription() {
            return longDescription;
        }

        public SubwayStatus setLongDescription(String longDescription) {
            this.longDescription = longDescription;
            return this;
        }

        public boolean isPlanned() {
            return isPlanned;
        }

        public SubwayStatus setPlanned(boolean planned) {
            isPlanned = planned;
            return this;
        }

        public String getReasonName() {
            return reasonName;
        }

        public SubwayStatus setReasonName(String reasonName) {
            this.reasonName = reasonName;
            return this;
        }

        public List<AffectedTrip> getAffectedTrips() {
            return affectedTrips;
        }

        public SubwayStatus setAffectedTrips(List<AffectedTrip> affectedTrips) {
            this.affectedTrips = affectedTrips;
            return this;
        }

        public String getTime() {
            return time;
        }

        public SubwayStatus setTime(String time) {
            this.time = time;
            return this;
        }
    }

    public List<SubwayStatus> getSubwayStatusList() {
        return subwayStatusList;
    }

    public SubwayStatusResponse setSubwayStatusList(List<SubwayStatus> subwayStatusList) {
        this.subwayStatusList = subwayStatusList;
        return this;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public SubwayStatusResponse setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }
}
