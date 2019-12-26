package com.transit.service.impl;

import com.transit.dao.MTASubwayDao;
import com.transit.dao.impl.MTASubwayGTFSDaoImpl;
import com.transit.domain.mta.*;
import com.transit.service.MTASubwayService;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class MTASubwayServiceImplTest {

    private MTASubwayDao mtaSubwayDao = Mockito.mock(MTASubwayGTFSDaoImpl.class);

    @Test
    public void listTrips() {
        MTASubwayService mtaSubwayService = new MTASubwayServiceImpl(mtaSubwayDao);
        when(mtaSubwayDao.getAvailableFeeds()).thenReturn(Arrays.asList(
                new Feed(1, Arrays.asList("1", "2")),
                new Feed(36, Arrays.asList("J", "Z")),
                new Feed(16, Arrays.asList("Q", "R", "W", "N")),
                new Feed(51, Arrays.asList("7"))
        ));
        doReturn(Arrays.asList(
                new Trip.builder("trip_id_1")
                        .withRouteId("1")
                        .headed(Trip.Direction.NORTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .forSubwayStation(new SubwayStation("1", "13B", "Jamaica - Van Wyck", "Queens", Arrays.asList(), 40.702566,-73.816859)) //0.7 miles
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 5) //5 minutes from now
                                        .build()))
                        .build(),
                new Trip.builder("trip_id_2")
                        .withRouteId("2")
                        .headed(Trip.Direction.SOUTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .forSubwayStation(new SubwayStation("2", "12B", "Cypress Hill", "Queens", Arrays.asList(), 40.710585, -73.799030)) //1.5
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 6) //6 minutes from now
                                        .build()))
                        .build()
        )).when(mtaSubwayDao).getTripsForFeed(1);

        doReturn(Arrays.asList(
                new Trip.builder("trip_id_J")
                        .withRouteId("J")
                        .headed(Trip.Direction.SOUTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 7) //7 minutes from now
                                        .forSubwayStation(new SubwayStation("3", "11B", "Crescent Hill", "Queens", Arrays.asList(), 40.712138, -73.794770)) //1.6 miles
                                        .build()))
                        .build(),
                new Trip.builder("trip_id_Z")
                        .withRouteId("Z")
                        .headed(Trip.Direction.NORTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 7) //7 minutes from now
                                        .forSubwayStation(new SubwayStation("3", "19B", "Crescent Hill", "Queens", Arrays.asList(), 40.712138, -73.794770)) //1.6 miles
                                        .build(),
                                new TripUpdate.builder()
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 10) //10 minutes from now
                                        .forSubwayStation(new SubwayStation("4", "95B", "Crescent Hill", "Queens", Arrays.asList(), 40.707308, -73.804405)) //1.16
                                        .build()))
                        .build()
        )).when(mtaSubwayDao).getTripsForFeed(36);

        doReturn(Arrays.asList(
                new Trip.builder("trip_id_Q")
                        .withRouteId("Q")
                        .headed(Trip.Direction.NORTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 8) //8 minutes from now
                                        .forSubwayStation(new SubwayStation("4", "95B", "Crescent Hill", "Queens", Arrays.asList(), 40.707308, -73.804405)) //1.16
                                        .build()))
                        .build()
        )).when(mtaSubwayDao).getTripsForFeed(16);

        doReturn(Arrays.asList(
                new Trip.builder("trip_id_7")
                        .withRouteId("7")
                        .headed(Trip.Direction.NORTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 50) //50 minutes from now
                                        .forSubwayStation(new SubwayStation("4", "45B", "Jackson Hts - Roosevelt", "Queens", Arrays.asList(), -1.707308, -73.804405)) //1.16
                                        .build()))
                        .build()
        )).when(mtaSubwayDao).getTripsForFeed(51);

        //route filter
        SubwayTripListParams listParams = new SubwayTripListParams.Builder()
                .withRoutes(Arrays.asList("J", "2"))
                .build();
        List<Trip> trips = mtaSubwayService.listTrips(listParams);
        assertEquals(2, trips.size());
        assertTrue(Arrays.asList("2", "J").containsAll(trips.stream().map(Trip::getRouteId).collect(Collectors.toList())));

        //trip id filter
        listParams = new SubwayTripListParams.Builder()
                .withTripIds(Arrays.asList("trip_id_2", "trip_id_Q", "trip_id_J"))
                .build();
        trips = mtaSubwayService.listTrips(listParams);
        assertEquals(3, trips.size());
        assertTrue(Arrays.asList("trip_id_2", "trip_id_Q", "trip_id_J").containsAll(trips.stream().map(Trip::getTripId).collect(Collectors.toList())));

        //stop id filter
        listParams = new SubwayTripListParams.Builder()
                .withGtfsStopIds(Arrays.asList("11B", "13B"))
                .build();
        trips = mtaSubwayService.listTrips(listParams);
        assertEquals(2, trips.size());
        assertTrue(trips
                .stream()
                .flatMap(t -> t.getTripUpdates().stream()
                        .map(u -> u.getSubwayStation().getGtfsStopId()))
                .collect(Collectors.toList()).containsAll(Arrays.asList("11B", "13B")));

        //geographic filter
        listParams = new SubwayTripListParams.Builder()
                .withLatitudeLongitude(40.7029319, -73.8258626)
                .withSearchRadius(1.5)
                .build();
        trips = mtaSubwayService.listTrips(listParams);
        assertEquals(4, trips.size());
        assertTrue(Arrays.asList("trip_id_2", "trip_id_Q", "trip_id_1", "trip_id_Z").containsAll(trips.stream().map(Trip::getTripId).collect(Collectors.toList())));

        //time filter - this filter will generally be useful in conjuction with location or station id filer
        listParams = new SubwayTripListParams.Builder()
                .withLatitudeLongitude(40.7029319, -73.8258626)
                .withSearchRadius(1.5)
                .withArrivingIn(8 * 1000 * 60)
                .build();
        trips = mtaSubwayService.listTrips(listParams);
        assertEquals(3, trips.size());
        assertTrue(Arrays.asList("trip_id_2", "trip_id_Q", "trip_id_1").containsAll(trips.stream().map(Trip::getTripId).collect(Collectors.toList())));

        //heading filter
        listParams = new SubwayTripListParams.Builder()
                .withRoutes(Arrays.asList("Z", "Q", "2"))
                .withDirection(Trip.Direction.NORTH)
                .build();
        trips = mtaSubwayService.listTrips(listParams);
        assertEquals(2, trips.size());
        assertTrue(Arrays.asList("trip_id_Z", "trip_id_Q").containsAll(trips.stream().map(Trip::getTripId).collect(Collectors.toList())));

        //try a complex filter
        listParams = new SubwayTripListParams.Builder()
                .withTripIds(Arrays.asList("trip_id_2", "trip_id_Q", "trip_id_Z"))
                .withRoutes(Arrays.asList("Z", "Q", "2", "1"))
                .withGtfsStopIds(Arrays.asList("95B", "13B"))
                .withDirection(Trip.Direction.NORTH)
                .withLatitudeLongitude(40.7029319, -73.8258626)
                .withSearchRadius(1.16)
                .withArrivingIn(1000 * 60 * 10)
                .build();
        trips = mtaSubwayService.listTrips(listParams);
        assertEquals(2, trips.size());
        assertTrue(Arrays.asList("trip_id_Z", "trip_id_Q").containsAll(trips.stream().map(Trip::getTripId).collect(Collectors.toList())));
    }
}