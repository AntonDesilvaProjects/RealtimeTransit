package com.transit.service.impl;

import com.transit.domain.mta.Feed;
import com.transit.domain.mta.Trip;
import com.transit.service.MTASubwayService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MTASubwayServiceImpl implements MTASubwayService {

    @Override
    public List<Feed> getAvailableFeeds() {
        return null;
    }

    @Override
    public List<Trip> getTrips() {
        return null;
    }

    @Override
    public List<Trip> getTripsForFeed(int feedId) {
        return null;
    }

    @Override
    public Trip getTrip(String routeId) {
        return null;
    }
}
