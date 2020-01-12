package com.transit.rest;

import com.transit.domain.mta.subway.SubwayStation;
import com.transit.domain.mta.subway.SubwayStatusResponse;
import com.transit.service.MTASubwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transit/mta/subway")
public class MTASubwayController {
    @Autowired
    private MTASubwayService subwayService;

    @GetMapping("/stations")
    public List<SubwayStation> getStations() {
        return subwayService.getSubwayStations();
    }

    @GetMapping("/status")
    public SubwayStatusResponse getStatus() { return subwayService.getSubwayStatus();}
}
