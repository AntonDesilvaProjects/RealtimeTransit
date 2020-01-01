package com.transit.service;

import com.transit.domain.mta.*;

import java.util.List;

public interface MTASubwayService {
    /**
     *  Gets a list of all available feeds. A feed consists of set of subway lines.
     * */
    List<Feed> getAvailableFeeds();

    /**
     *  Gets a list of subway stations using specified list params
     * */
    List<SubwayStation> listStations(SubwayStationListParams listParams);

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
    List<Trip> getTrips(String routeId);

    /**
     *  Gets a list of all current trips using the specified list params
     * */
    List<Trip> listTrips(SubwayTripListParams listParams);

    /**
     *  Get a list of all subway stations.
     *
     *  TODO: Need to implement sorting and filtering
     * */
    List<SubwayStation> getSubwayStations();
}
