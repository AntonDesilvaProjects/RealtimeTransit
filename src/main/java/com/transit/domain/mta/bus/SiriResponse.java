package com.transit.domain.mta.bus;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class SiriResponse {
    @JsonAlias("Siri")
    private Siri siri;

    public SiriResponse.Siri getSiri() {
        return siri;
    }

    public SiriResponse setSiri(SiriResponse.Siri siri) {
        this.siri = siri;
        return this;
    }

    public static class Siri {
        @JsonAlias("ServiceDelivery")
        private ServiceDelivery serviceDelivery;

        public ServiceDelivery getServiceDelivery() {
            return serviceDelivery;
        }

        public Siri setServiceDelivery(ServiceDelivery serviceDelivery) {
            this.serviceDelivery = serviceDelivery;
            return this;
        }
    }

    public static class ServiceDelivery {
        @JsonAlias({"StopMonitoringDelivery", "VehicleMonitoringDelivery"})
        private List<ServiceDeliveryUpdate> serviceDeliveryUpdate;

        @JsonAlias("SituationExchangeDelivery")
        private List<SituationDeliveryUpdate> situationExchangeDelivery;

        public List<ServiceDeliveryUpdate> getServiceDeliveryUpdate() {
            return serviceDeliveryUpdate;
        }

        public ServiceDelivery setServiceDeliveryUpdate(List<ServiceDeliveryUpdate> serviceDeliveryUpdate) {
            this.serviceDeliveryUpdate = serviceDeliveryUpdate;
            return this;
        }

        public List<SituationDeliveryUpdate> getSituationExchangeDelivery() {
            return situationExchangeDelivery;
        }

        public ServiceDelivery setSituationExchangeDelivery(List<SituationDeliveryUpdate> situationExchangeDelivery) {
            this.situationExchangeDelivery = situationExchangeDelivery;
            return this;
        }
    }

    public static class ServiceDeliveryUpdate {
        @JsonAlias({"VehicleActivity", "MonitoredStopVisit"})
        private List<RealTimeUpdate> updates;
        @JsonAlias("ResponseTimestamp")
        private String responseTimestamp;
        @JsonAlias("ErrorCondition")
        private ErrorCondition errorCondition;

        public String getResponseTimestamp() {
            return responseTimestamp;
        }
        public ServiceDeliveryUpdate setResponseTimestamp(String responseTimestamp) {
            this.responseTimestamp = responseTimestamp;
            return this;
        }
        public List<RealTimeUpdate> getUpdates() {
            return updates;
        }
        public ServiceDeliveryUpdate setUpdates(List<RealTimeUpdate> updates) {
            this.updates = updates;
            return this;
        }

        public ErrorCondition getErrorCondition() {
            return errorCondition;
        }

        public ServiceDeliveryUpdate setErrorCondition(ErrorCondition errorCondition) {
            this.errorCondition = errorCondition;
            return this;
        }
    }

    public static class ErrorCondition {
        @JsonAlias("Description")
        private String description;
        private String errorText;

        @JsonProperty("OtherError")
        public void unwrapErroText(Map<String, String> otherErrorMap) {
            this.errorText = otherErrorMap.get("ErrorText");
        }

        public String getDescription() {
            return description;
        }

        public ErrorCondition setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getErrorText() {
            return errorText;
        }

        public ErrorCondition setErrorText(String errorText) {
            this.errorText = errorText;
            return this;
        }
    }

    public static class RealTimeUpdate {
        @JsonAlias("MonitoredVehicleJourney")
        private MonitoredVehicleJourney monitoredVehicleJourney;
        @JsonAlias("RecordedAtTime")
        private String recordedAtTime;

        public MonitoredVehicleJourney getMonitoredVehicleJourney() {
            return monitoredVehicleJourney;
        }

        public RealTimeUpdate setMonitoredVehicleJourney(MonitoredVehicleJourney monitoredVehicleJourney) {
            this.monitoredVehicleJourney = monitoredVehicleJourney;
            return this;
        }

        public String getRecordedAtTime() {
            return recordedAtTime;
        }

        public RealTimeUpdate setRecordedAtTime(String recordedAtTime) {
            this.recordedAtTime = recordedAtTime;
            return this;
        }
    }

    public static class SituationDeliveryUpdate {
        @JsonProperty("Situations")
        private SituationsObject situations;

        public SituationsObject getSituations() {
            return situations;
        }

        public SituationDeliveryUpdate setSituations(SituationsObject situations) {
            this.situations = situations;
            return this;
        }
    }

    public static class SituationsObject {
        @JsonAlias("PtSituationElement")
        private List<Situation> situations;
        public List<Situation> getSituations() {
            return situations;
        }
        public SituationsObject setSituations(List<Situation> situations) {
            this.situations = situations;
            return this;
        }
    }
}
