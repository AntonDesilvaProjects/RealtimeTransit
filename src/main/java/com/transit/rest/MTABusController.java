package com.transit.rest;

import com.transit.domain.mta.bus.*;
import com.transit.domain.mta.subway.Trip;
import com.transit.service.MTABusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.transit.TransitConstants.DOUBLE_MIN_VALUE_STRING;

@RestController
@RequestMapping("/transit/mta/bus")
@CrossOrigin(origins = "*")
public class MTABusController {

    @Autowired
    private MTABusService busService;

    @GetMapping("/routes")
    public List<Route> getRoutes(@RequestParam(value = "latitude", required = false, defaultValue = DOUBLE_MIN_VALUE_STRING) Double latitude,
                                 @RequestParam(value = "longitude", required = false, defaultValue = DOUBLE_MIN_VALUE_STRING) Double longitude,
                                 @RequestParam(value = "searchRadius", required = false, defaultValue = "1") Double searchRadius,
                                 @RequestParam(value = "query", required = false) String query) {
        //return a list of routes based on the passed list params
        return busService.listRoutes(new BusListParams.Builder()
                .withLatitude(latitude)
                .withLongitude(longitude)
                .withSearchRadius(searchRadius)
                .withQuery(query)
                .build());
    }

    @GetMapping("/stops")
    public StopResponse.Entry getStops(@RequestParam(value = "routeId", required = false) String routeId,
                                       @RequestParam(value = "latitude", required = false, defaultValue = DOUBLE_MIN_VALUE_STRING) Double latitude,
                                       @RequestParam(value = "longitude", required = false, defaultValue = DOUBLE_MIN_VALUE_STRING) Double longitude,
                                       @RequestParam(value = "searchRadius", required = false, defaultValue = "1") Double searchRadius,
                                       @RequestParam(value = "query", required = false) String query) {
        //return a list of trips based on the passed list params
        return busService.listStops(new BusListParams.Builder()
                .withRouteId(routeId)
                .withLatitude(latitude)
                .withLongitude(longitude)
                .withSearchRadius(searchRadius)
                .withQuery(query)
                .build());
    }

    @GetMapping("/trips")
    public SiriResponse getActiveRouteInformation(@RequestParam(value = "routeId", required = false) String routeId,
                                                  @RequestParam(value = "stopId", required = false) String stopId,
                                                  @RequestParam(value = "vehicleId", required = false) String vehicleId,
                                                  @RequestParam(value = "direction", required = false) String direction) {
        return busService.listTrips(new BusListParams.Builder()
                .withRouteId(routeId)
                .withStopId(stopId)
                .withRouteId(routeId)
                .withVehicleId(vehicleId)
                .withDirection(Optional.ofNullable(direction).map(Trip.Direction::fromString).orElse(null))
                .build());
    }
}
