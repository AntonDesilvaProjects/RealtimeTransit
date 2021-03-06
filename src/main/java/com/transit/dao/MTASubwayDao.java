package com.transit.dao;

import com.transit.domain.mta.subway.Feed;
import com.transit.domain.mta.subway.SubwayStation;
import com.transit.domain.mta.subway.SubwayStatusResponse;
import com.transit.domain.mta.subway.Trip;

import java.util.List;

public interface MTASubwayDao {
    /**
     *  Gets a list of all current trips from all available feeds
     * */
    List<Feed> getAvailableFeeds();

    /**
     *  Gets a list of all current trips from the specified feed
     * */
    List<Trip> getTripsForFeed(int feedId);

    /**
     *  Gets a list of all subway stations
     * */
    List<SubwayStation> getSubwayStations();

    /**
     *  Gets the status of the subway system including delays, planned work, and other service alerts
     * */
    SubwayStatusResponse getSubwayStatus();

}

