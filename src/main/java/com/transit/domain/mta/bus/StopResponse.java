package com.transit.domain.mta.bus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StopResponse {

    private Entry entry;
    private References references;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Entry {
        private String routeId;
        private List<StopGrouping> stopGroupings;

        public String getRouteId() {
            return routeId;
        }

        public Entry setRouteId(String routeId) {
            this.routeId = routeId;
            return this;
        }

        public List<StopGrouping> getStopGroupings() {
            return stopGroupings;
        }

        public Entry setStopGroupings(List<StopGrouping> stopGroupings) {
            this.stopGroupings = stopGroupings;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class References {
        private List<Route> routes;
        private List<Stop> stops;

        public List<Route> getRoutes() {
            return routes;
        }

        public References setRoutes(List<Route> routes) {
            this.routes = routes;
            return this;
        }

        public List<Stop> getStops() {
            return stops;
        }

        public References setStops(List<Stop> stops) {
            this.stops = stops;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StopGrouping {
        private List<Group> stopGroups;

        public List<Group> getStopGroups() {
            return stopGroups;
        }

        public StopGrouping setStopGroups(List<Group> stopGroups) {
            this.stopGroups = stopGroups;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Group {
        private String id;
        private String groupName;
        private String type;
        private List<String> stopIds;
        private List<Stop> stops;

        @JsonProperty("name")
        private void unpackNameAndType(Map<String, Object> nameObject) {
            this.groupName = (String)nameObject.get("name");
            this.type = (String)nameObject.get("type");
        }
        public String getId() {
            return id;
        }

        public Group setId(String id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return groupName;
        }

        public Group setName(String name) {
            this.groupName = name;
            return this;
        }

        public String getType() {
            return type;
        }

        public Group setType(String type) {
            this.type = type;
            return this;
        }

        public List<String> getStopIds() {
            return stopIds;
        }

        public Group setStopIds(List<String> stopIds) {
            this.stopIds = stopIds;
            return this;
        }

        public String getGroupName() {
            return groupName;
        }

        public Group setGroupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public List<Stop> getStops() {
            return stops;
        }

        public Group setStops(List<Stop> stops) {
            this.stops = stops;
            return this;
        }
    }

    public Entry getEntry() {
        return entry;
    }

    public StopResponse setEntry(Entry entry) {
        this.entry = entry;
        return this;
    }

    public References getReferences() {
        return references;
    }

    public StopResponse setReferences(References references) {
        this.references = references;
        return this;
    }
}
