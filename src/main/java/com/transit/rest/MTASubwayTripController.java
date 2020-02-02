package com.transit.rest;

import com.transit.domain.mta.subway.SubwayTripListParams;
import com.transit.domain.mta.subway.Trip;
import com.transit.service.MTASubwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.transit.TransitConstants.DOUBLE_MIN_VALUE_STRING;
import static com.transit.TransitConstants.LONG_MIN_VALUE_STRING;

@RestController
@RequestMapping("/transit/mta/subway/trips")
@CrossOrigin(origins = "*")
public class MTASubwayTripController {

    @Autowired
    private MTASubwayService subwayService;

    @GetMapping
    public List<Trip> getTrips() {
        return subwayService.getTrips();
    }

    @GetMapping("/{route}")
    public List<Trip> getTripsForRoute(@PathVariable("route") String route) {
        return subwayService.getTrips(route);
    }

    @GetMapping("/list")
    public List<Trip> list(@RequestParam(value = "routes", required = false) List<String> routes,
                           @RequestParam(value = "tripIds", required = false) List<String> tripIds,
                           @RequestParam(value = "stopIds", required = false) List<String> stopIds,
                           @RequestParam(value = "latitude", required = false, defaultValue = DOUBLE_MIN_VALUE_STRING) Double latitude,
                           @RequestParam(value = "longitude", required = false, defaultValue = DOUBLE_MIN_VALUE_STRING) Double longitude,
                           @RequestParam(value = "searchRadius", required = false, defaultValue = "1") Double searchRadius,
                           @RequestParam(value = "direction", required = false) String direction,
                           @RequestParam(value = "arrivingIn", required = false, defaultValue = LONG_MIN_VALUE_STRING) Long arrivingIn,
                           @RequestParam(value = "sortBy", required = false) String sortString) {

        SubwayTripListParams subwayTripListParams = new SubwayTripListParams.Builder()
                .withRoutes(routes)
                .withTripIds(tripIds)
                .withGtfsStopIds(stopIds)
                .withLatitudeLongitude(latitude, longitude)
                .withSearchRadius(searchRadius)
                .withDirection(Optional.ofNullable(direction).map(Trip.Direction::fromString).orElse(null))
                .withArrivingIn(arrivingIn)
                .sortedByFromString(sortString)
                .build();
        return subwayService.listTrips(subwayTripListParams);
    }

}
