package com.transit.dao;

import com.transit.domain.mta.Feed;
import com.transit.domain.mta.SubwayStation;
import com.transit.domain.mta.Trip;

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

}

