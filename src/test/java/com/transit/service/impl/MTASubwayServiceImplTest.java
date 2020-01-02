package com.transit.service.impl;

import com.transit.dao.MTASubwayDao;
import com.transit.dao.impl.MTASubwayGTFSDaoImpl;
import com.transit.domain.mta.subway.*;
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

    @Test
    public void sortTripsByRoute() {
        List<Trip> tripList = Arrays.asList(
                new Trip.builder("123").withRouteId("F").build(),
                new Trip.builder("124").withRouteId("1").build(),
                new Trip.builder("125").withRouteId("A").build(),
                new Trip.builder("126").withRouteId("F").build()
        );

        SubwayTripListParams listParams = new SubwayTripListParams.Builder().build();
        List<Trip> sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        List<String> correctOrder = Arrays.asList("F", "1", "A", "F");
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getRouteId());
        }

        listParams = new SubwayTripListParams
                .Builder()
                .sortedBy(Sorting.Field.ROUTE, Sorting.Order.ASCENDING).build();
        sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        correctOrder = Arrays.asList("1", "A", "F", "F");
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getRouteId());
        }

        listParams = new SubwayTripListParams
                .Builder()
                .sortedBy(Sorting.Field.ROUTE, Sorting.Order.DESCENDING).build();
        sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        correctOrder = Arrays.asList("F", "F", "A", "1");
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getRouteId());
        }
    }

    @Test
    public void sortTripsByDistance() {
        List<Trip> tripList = Arrays.asList(
                //760
                new Trip.builder("trip_id_J")
                        .withRouteId("J")
                        .headed(Trip.Direction.NORTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .forSubwayStation(new SubwayStation("1", "13B", "Jamaica - Van Wyck", "Queens", Arrays.asList(), 40.702566,-73.816859)) //0.7 miles
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 5) //5 minutes from now
                                        .build()))
                        .build(),
                //2416
                new Trip.builder("trip_id_2")
                        .withRouteId("2")
                        .headed(Trip.Direction.SOUTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .forSubwayStation(new SubwayStation("2", "12B", "Cypress Hill", "Queens", Arrays.asList(), 40.710585, -73.799030)) //1.5
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 6) //6 minutes from now
                                        .build()))
                        .build(),
                //2813
                new Trip.builder("trip_id_1")
                        .withRouteId("1")
                        .headed(Trip.Direction.SOUTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 7) //7 minutes from now
                                        .forSubwayStation(new SubwayStation("3", "11B", "Crescent Hill", "Queens", Arrays.asList(), 40.712138, -73.794770)) //1.6 miles
                                        .build()))
                        .build(),
                //1873
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
        );

        //mark all the trips as a matched search for testing
        tripList.stream().flatMap(t -> t.getTripUpdates().stream()).forEach(u -> u.setMatchedSearch(true));

        SubwayTripListParams listParams = new SubwayTripListParams.Builder().build();
        List<Trip> sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        List<String> correctOrder = Arrays.asList("J", "2", "1", "Z");
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getRouteId());
        }

        listParams = new SubwayTripListParams
                .Builder()
                .withLatitudeLongitude(40.7029319, -73.8258626)
                .sortedBy(Sorting.Field.DISTANCE, Sorting.Order.ASCENDING).build();
        sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        correctOrder = Arrays.asList("J", "Z", "2", "1");
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getRouteId());
        }

        listParams = new SubwayTripListParams
                .Builder()
                .withLatitudeLongitude(40.7029319, -73.8258626)
                .sortedBy(Sorting.Field.DISTANCE, Sorting.Order.DESCENDING).build();
        sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        correctOrder = Arrays.asList("1", "2", "Z", "J");
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getRouteId());
        }
    }

    @Test
    public void sortTripsByArrivalTime() {
        List<Trip> tripList = Arrays.asList(
                //15 minutes from now
                new Trip.builder("trip_id_J")
                        .withRouteId("J")
                        .headed(Trip.Direction.NORTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .forSubwayStation(new SubwayStation("1", "13B", "Jamaica - Van Wyck", "Queens", Arrays.asList(), 40.702566,-73.816859)) //0.7 miles
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 15)
                                        .build()))
                        .build(),
                //6 minutes from now
                new Trip.builder("trip_id_2")
                        .withRouteId("2")
                        .headed(Trip.Direction.SOUTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .forSubwayStation(new SubwayStation("2", "12B", "Cypress Hill", "Queens", Arrays.asList(), 40.710585, -73.799030)) //1.5
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 6)
                                        .build()))
                        .build(),
                //1 minutes from now
                new Trip.builder("trip_id_1")
                        .withRouteId("1")
                        .headed(Trip.Direction.SOUTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 1)
                                        .forSubwayStation(new SubwayStation("3", "11B", "Crescent Hill", "Queens", Arrays.asList(), 40.712138, -73.794770)) //1.6 miles
                                        .build()))
                        .build(),
                //7 minutes from now
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
        );

        //mark all the trips as a matched search for testing
        tripList.stream().flatMap(t -> t.getTripUpdates().stream()).forEach(u -> u.setMatchedSearch(true));

        SubwayTripListParams listParams = new SubwayTripListParams.Builder().build();
        List<Trip> sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        List<String> correctOrder = Arrays.asList("J", "2", "1", "Z");
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getRouteId());
        }

        listParams = new SubwayTripListParams
                .Builder()
                .withArrivingIn(System.currentTimeMillis() + 1000 * 60 * 20)
                .sortedBy(Sorting.Field.ARRIVAL_TIME, Sorting.Order.ASCENDING).build();
        sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        correctOrder = Arrays.asList("1", "2", "Z", "J");
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getRouteId());
        }

        listParams = new SubwayTripListParams
                .Builder()
                .withArrivingIn(System.currentTimeMillis() + 1000 * 60 * 20)
                .sortedBy(Sorting.Field.ARRIVAL_TIME, Sorting.Order.DESCENDING).build();
        sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        correctOrder = Arrays.asList("J", "Z", "2", "1");
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getRouteId());
        }
    }

    //J, Z =>
    @Test
    public void compoundSorting() {
        List<Trip> tripList = Arrays.asList(
                //<-----------------------------------------------------------
                //J, 15 minutes from now, 760, trip_id_J
                new Trip.builder("trip_id_J")
                        .withRouteId("J")
                        .headed(Trip.Direction.NORTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .forSubwayStation(new SubwayStation("1", "13B", "Jamaica - Van Wyck", "Queens", Arrays.asList(), 40.702566,-73.816859)) //0.7 miles
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 15)
                                        .build()))
                        .build(),
                //----------------------------------------------------------->

                //2, 6 minutes from now, 2416, trip_id_2
                new Trip.builder("trip_id_2")
                        .withRouteId("2")
                        .headed(Trip.Direction.SOUTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .forSubwayStation(new SubwayStation("2", "12B", "Cypress Hill", "Queens", Arrays.asList(), 40.710585, -73.799030)) //1.5
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 6)
                                        .build()))
                        .build(),

                //<-----------------------------------------------------------
                //Z, 1 minutes from now, 1873.0620886538184, trip_id_Z_2
                new Trip.builder("trip_id_Z_2")
                        .withRouteId("Z")
                        .headed(Trip.Direction.NORTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 5) //5 minutes from now
                                        .forSubwayStation(new SubwayStation("3", "19B", "Crescent Hill", "Queens", Arrays.asList(), 40.712138, -73.794770)) //1.6 miles
                                        .build(),
                                new TripUpdate.builder()
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 1) //1 minutes from now
                                        .forSubwayStation(new SubwayStation("4", "95B", "Crescent Hill", "Queens", Arrays.asList(), 40.707308, -73.804405)) //1.16
                                        .build()))
                        .build(),

                //----------------------------------------------------------->

                //1, 1 minutes from now, 2813.6558993433537, trip_id_1
                new Trip.builder("trip_id_1")
                        .withRouteId("1")
                        .headed(Trip.Direction.SOUTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 1)
                                        .forSubwayStation(new SubwayStation("3", "11B", "Crescent Hill", "Queens", Arrays.asList(), 40.712138, -73.794770)) //1.6 miles
                                        .build()))
                        .build(),


                //Z, 7 minutes from now, 1873.0620886538184, trip_id_Z
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
                        .build(),

                //2, 2 minutes from now, 2416, trip_id_2_2
                new Trip.builder("trip_id_2_2")
                        .withRouteId("2")
                        .headed(Trip.Direction.SOUTH)
                        .havingTripUpdates(Arrays.asList(
                                new TripUpdate.builder()
                                        .forSubwayStation(new SubwayStation("2", "12B", "Cypress Hill", "Queens", Arrays.asList(), 40.712138, -73.794770)) //1.5
                                        .arrivingOn(System.currentTimeMillis() + 1000 * 60 * 2)
                                        .build()))
                        .build()
        );

        //mark all the trips as a matched search for testing
        tripList.stream().flatMap(t -> t.getTripUpdates().stream()).forEach(u -> u.setMatchedSearch(true));

        //order by route, then distance, and arrival time

        SubwayTripListParams listParams = new SubwayTripListParams
                .Builder()
                .withArrivingIn(System.currentTimeMillis() + 1000 * 60 * 20)
                .withLatitudeLongitude(40.7029319, -73.8258626)
                .withRoutes(Arrays.asList("J", "2", "1", "Z"))
                .sortedBy(Sorting.Field.ROUTE, Sorting.Order.ASCENDING)
                .sortedBy(Sorting.Field.DISTANCE, Sorting.Order.ASCENDING)
                .sortedBy(Sorting.Field.ARRIVAL_TIME, Sorting.Order.ASCENDING).build();
        List<Trip> sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        List<String> correctOrder = Arrays.asList("trip_id_1", "trip_id_2", "trip_id_2_2", "trip_id_J", "trip_id_Z_2", "trip_id_Z" );
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getTripId());
        }

        listParams = new SubwayTripListParams
                .Builder()
                .withArrivingIn(System.currentTimeMillis() + 1000 * 60 * 20)
                .withLatitudeLongitude(40.7029319, -73.8258626)
                .withRoutes(Arrays.asList("J", "2", "1", "Z"))
                .sortedBy(Sorting.Field.ROUTE, Sorting.Order.DESCENDING)
                .sortedBy(Sorting.Field.DISTANCE, Sorting.Order.DESCENDING)
                .sortedBy(Sorting.Field.ARRIVAL_TIME, Sorting.Order.ASCENDING).build();
        sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        correctOrder = Arrays.asList("trip_id_Z_2", "trip_id_Z", "trip_id_J", "trip_id_2_2", "trip_id_2", "trip_id_1");
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getTripId());
        }

        listParams = new SubwayTripListParams
                .Builder()
                .withArrivingIn(System.currentTimeMillis() + 1000 * 60 * 20)
                .withLatitudeLongitude(40.7029319, -73.8258626)
                .withRoutes(Arrays.asList("J", "2", "1", "Z"))
                .sortedBy(Sorting.Field.ROUTE, Sorting.Order.DESCENDING)
                .sortedBy(Sorting.Field.DISTANCE, Sorting.Order.ASCENDING)
                .sortedBy(Sorting.Field.ARRIVAL_TIME, Sorting.Order.DESCENDING).build();
        sortedTrips = listParams.buildSorter().map(tripList.stream()::sorted).orElse(tripList.stream()).collect(Collectors.toList());
        correctOrder = Arrays.asList("trip_id_Z", "trip_id_Z_2", "trip_id_J", "trip_id_2", "trip_id_2_2", "trip_id_1");
        for (int i = 0; i < correctOrder.size(); i++) {
            assertEquals(correctOrder.get(i), sortedTrips.get(i).getTripId());
        }
    }
}