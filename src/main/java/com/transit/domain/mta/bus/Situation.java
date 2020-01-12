package com.transit.domain.mta.bus;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public class Situation {
    @JsonAlias("SituationNumber")
    private String situationId;
    @JsonAlias("Severity")
    private String severity;
    @JsonAlias("Summary")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> summary;
    @JsonAlias("Description")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> description;

    public String getSituationId() {
        return situationId;
    }

    public Situation setSituationId(String situationId) {
        this.situationId = situationId;
        return this;
    }

    public String getSeverity() {
        return severity;
    }

    public Situation setSeverity(String severity) {
        this.severity = severity;
        return this;
    }

    public List<String> getSummary() {
        return summary;
    }

    public Situation setSummary(List<String> summary) {
        this.summary = summary;
        return this;
    }

    public List<String> getDescription() {
        return description;
    }

    public Situation setDescription(List<String> description) {
        this.description = description;
        return this;
    }
}
