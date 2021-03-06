package com.transit.service.impl;

import com.transit.TransitConstants;
import com.transit.dao.MTASubwayDao;
import com.transit.domain.mta.subway.*;
import com.transit.service.MTASubwayService;
import com.transit.utils.GeoUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<SubwayStation> listStations(SubwayStationListParams listParams) {
        //we want to filter using: stop_id, location, trains_supported, borough search, full_text search
        List<SubwayStation> subwayStations = mtaSubwayDao.getSubwayStations();
        return subwayStations;
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
        //filter out the feeds based on the routes
        Set<Integer> feeds;
        if (listParams.isRouteSearch()) {
            feeds = listParams.getRoutes()
                    .stream()
                    .map(route -> Optional.ofNullable(TransitConstants.ROUTE_FEED_MAP.get(route))
                            .orElseThrow(() -> new IllegalArgumentException(String.format("%s is an invalid route!", route)))
                            .getFeedId())
                    .collect(Collectors.toSet());
        } else {
            feeds = getAvailableFeeds().stream().map(Feed::getFeedId).collect(Collectors.toSet());
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
            //also mark the trip update that matched the search
            //note that we are not short-circuiting
            return trip.getTripUpdates()
                    .stream()
                    .filter(tripUpdate -> !listParams.includesStopIds() || listParams.getGtfsStopIds().contains(tripUpdate.getSubwayStation().getGtfsStopId()))
                    .filter(tripUpdate -> !listParams.isLocationSearch() || GeoUtils.isWithinRadius(tripUpdate.getSubwayStation().getGtfsLatitude(), tripUpdate.getSubwayStation().getGetGtfsLongitude(), listParams.getLatitude(), listParams.getLongitude(), listParams.getSearchRadius()))
                    .filter(tripUpdate -> tripUpdate.setMatchedSearch(!listParams.isTimeSearch() || tripUpdate.getArrivalTime() - System.currentTimeMillis() <= listParams.getArrivingIn())).count() > 0;
        };
        Stream<Trip> filteredTripStream = feeds
                .parallelStream()
                .flatMap(feedId -> getTripsForFeed(feedId).stream())
                .peek(trip -> {
                        if (listParams.isLocationSearch() && !CollectionUtils.isEmpty(trip.getTripUpdates())) {
                            trip.getTripUpdates().forEach(tripUpdate -> tripUpdate.setDistance(GeoUtils.distance(
                                    tripUpdate.getSubwayStation().getGtfsLatitude(),
                                    listParams.getLatitude(),
                                    tripUpdate.getSubwayStation().getGetGtfsLongitude(),
                                    listParams.getLongitude())));
                        }
                })
                .filter(routeFilter.and(tripIdFilter).and(stationFilter).and(directionFilter));

       return listParams.buildSorter().map(filteredTripStream::sorted).orElse(filteredTripStream).collect(Collectors.toList());
//       t.forEach(trip -> {
//           if (trip.getDirection() == Trip.Direction.SOUTH && trip.getRouteId().equalsIgnoreCase("E")
//                   && trip.getTripUpdates().stream().anyMatch(u -> u.getSubwayStation().getGtfsStopId().equalsIgnoreCase("F06")) ) {
//               TripUpdate k = trip.getTripUpdates().stream().filter(u -> u.getSubwayStation().getGtfsStopId().equalsIgnoreCase("F06")).findFirst().get();
//               System.out.println(trip.getRouteId() + "/" + k.getSubwayStation().getName() + "/" + trip.getDestination() + "/" + time(k.getArrivalTime()));
//           }
//       });
      // return t;
    }

    public static String time(long t) {
        Instant instant = Instant.ofEpochMilli(t);
        LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return date.toString();
    }

    @Override
    public List<SubwayStation> getSubwayStations() {
        return mtaSubwayDao.getSubwayStations();
    }

    @Override
    public SubwayStatusResponse getSubwayStatus() {
      return mtaSubwayDao.getSubwayStatus();
    }
}
