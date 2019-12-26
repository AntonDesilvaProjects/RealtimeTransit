package com.transit.rest;

import com.transit.domain.mta.Trip;
import com.transit.service.MTASubwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transit/mta/subway")
public class MTASubwayController {

    @Autowired
    private MTASubwayService subwayService;

    @GetMapping
    public List<Trip> getTrips() {
        long startTime = System.nanoTime();
        List<Trip> results = subwayService.getTrips();
        long endTime = System.nanoTime();
        System.out.println("That took " + (endTime - startTime) / 1000000 + " milliseconds");
        return results;
    }

    @GetMapping("/{route}")
    public List<Trip> getTripsForRoute(@PathVariable("route") String route) {
        return subwayService.getTrips(route);
    }
}
