package com.transit.service.impl;

import com.transit.TransitConstants;
import com.transit.dao.MTASubwayDao;
import com.transit.domain.mta.Feed;
import com.transit.domain.mta.Trip;
import com.transit.domain.mta.SubwayTripListParams;
import com.transit.service.MTASubwayService;
import com.transit.utils.GeoUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class MTASubwayServiceImpl implements MTASubwayService {

    private final MTASubwayDao mtaSubwayDao;

    @Autowired
    public MTASubwayServiceImpl(MTASubwayDao mtaSubwayDao) {
        this.mtaSubwayDao = mtaSubwayDao;
    }

    @Override
    public List<Feed> getAvailableFeeds() {
        return mtaSubwayDao.getAvailableFeeds();
    }

    @Override
    public List<Trip> getTrips() {
        return getAvailableFeeds().parallelStream().flatMap(f -> getTripsForFeed(f.getFeedId()).stream()).collect(Collectors.toList());
    }

    @Override
    public List<Trip> getTripsForFeed(int feedId) {
        return mtaSubwayDao.getTripsForFeed(feedId);
    }

    @Override
    public List<Trip> getTrips(String routeId) {
       return getTripsForFeed(getAvailableFeeds()
               .stream()
               .filter(i -> i.getLines().contains(StringUtils.capitalize(routeId)))
               .findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("'%s' is not a valid route!", routeId))).getFeedId())
                   .stream()
                   .filter(t -> t.getRouteId().equalsIgnoreCase(routeId))
                   .collect(Collectors.toList());
    }

    @Override
    public List<Trip> listTrips(SubwayTripListParams listParams) {
        //filter out the feeds
        //Predicate<Feed> feedFilter = feed -> !listParams.isRouteSearch() || feed.getLines().stream().anyMatch(line -> listParams.getRoutes().contains(line));
        //List<Integer> feeds = getAvailableFeeds().stream().filter(feedFilter).map(Feed::getFeedId).collect(Collectors.toList());
        List<Integer> feeds;
        if (listParams.isRouteSearch()) {
            feeds = listParams.getRoutes()
                    .stream()
                    .map(route -> Optional.ofNullable(TransitConstants.ROUTE_FEED_MAP.get(route))
                            .orElseThrow(() -> new IllegalArgumentException(String.format("%s is an invalid route!", route)))
                            .getFeedId())
                    .collect(Collectors.toList());
            //throw new IllegalArgumentException(String.format("One or more of the provided routes['%s'] are not valid!", listParams.getRoutes()));
        } else {
            feeds = getAvailableFeeds().stream().map(Feed::getFeedId).collect(Collectors.toList());
        }
        //build a complex predicate to filter out trips based on the search params
        Predicate<Trip> routeFilter = trip -> !listParams.isRouteSearch() || listParams.getRoutes().contains(trip.getRouteId());
        Predicate<Trip> tripIdFilter = trip -> !listParams.isTripIdSearch() || listParams.getTripIds().contains(trip.getTripId());
        Predicate<Trip> directionFilter = trip -> !listParams.includesHeading() || trip.getDirection() == listParams.getDirection();
        Predicate<Trip> stationFilter = trip -> {
            if ( !(listParams.includesStopIds() || listParams.isLocationSearch())) {
                //include all the trips because we can't limit trips based on trip updates if we have no criteria for stations
                //a trip update only has meaning in context of a station
                return true;
            }
            //get a list of trip updates which has the searched stations(either via direct station id or geographic location)
            return trip.getTripUpdates().stream()
                    .filter(tripUpdate -> !listParams.includesStopIds() || listParams.getGtfsStopIds().contains(tripUpdate.getSubwayStation().getGtfsStopId()))
                    .filter(tripUpdate -> !listParams.isLocationSearch() || GeoUtils.isWithinRadius(tripUpdate.getSubwayStation().getGtfsLatitude(), tripUpdate.getSubwayStation().getGetGtfsLongitude(), listParams.getLatitude(), listParams.getLongitude(), listParams.getSearchRadius()))
                    .anyMatch(tripUpdate -> !listParams.isTimeSearch() || tripUpdate.getArrivalTime() - System.currentTimeMillis() <= listParams.getArrivingIn());
            //once we have the stations, check if user searched for arrival time and filter out the non-matching stations
            //if we have at least one station return true(to include this trip) otherwise false
        };
        return feeds
                .parallelStream()
                .flatMap(feedId -> getTripsForFeed(feedId).stream())
                .filter(routeFilter.and(tripIdFilter).and(stationFilter).and(directionFilter))
                .collect(Collectors.toList());
    }


}
