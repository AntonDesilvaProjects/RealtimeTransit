package com.transit.service;

import com.transit.domain.mta.Feed;
import com.transit.domain.mta.Trip;

import java.util.List;

public interface MTASubwayService {
    /**
     *  Gets a list of all current trips from all available feeds
     * */
    List<Feed> getAvailableFeeds();

    /**
     *  Gets a list of all current trips from all available feeds
     * */
    List<Trip> getTrips();
    /**
     *  Gets a list of all current trips from the specified feed
     * */
    List<Trip> getTripsForFeed(int feedId);
    /**
     *  Gets a list of all current trips for the specified route(i.e. F route)
     * */
    Trip getTrip(String routeId);
}
