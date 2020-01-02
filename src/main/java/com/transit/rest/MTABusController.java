package com.transit.rest;

import com.transit.domain.mta.bus.BusListParams;
import com.transit.domain.mta.bus.Route;
import com.transit.service.MTABusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.transit.TransitConstants.DOUBLE_MIN_VALUE_STRING;

@RestController
@RequestMapping("/transit/mta/bus")
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
    public void getStops() {
        //return a list of stops based on the passed list params
    }

    @GetMapping("/stops/updates/{stopId}")
    public void getUpdatesForStop() {
        //for the given stop id, fetch updates
    }


    @GetMapping("/updates")
    public void getActiveRouteInformation() {
        //pull information about an entire route or a specific bus
    }
}
