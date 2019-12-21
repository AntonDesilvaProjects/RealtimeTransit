package com.transit.domain.mta;

import java.util.List;

public class Feed {
    private int feedId;
    private List<String> lines;

    public Feed(int feedId, List<String> lines){
        this.feedId = feedId;
        this.lines = lines;
    }

    public int getFeedId() {
        return feedId;
    }

    public Feed setFeedId(int feedId) {
        this.feedId = feedId;
        return this;
    }

    public List<String> getLines() {
        return lines;
    }

    public Feed setLines(List<String> lines) {
        this.lines = lines;
        return this;
    }
}
