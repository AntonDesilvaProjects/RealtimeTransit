package com.transit.service.impl;

import com.transit.dao.MTABusServiceDao;
import com.transit.domain.mta.bus.BusListParams;
import com.transit.domain.mta.bus.Route;
import com.transit.domain.mta.bus.SiriResponse;
import com.transit.domain.mta.bus.StopResponse;
import com.transit.domain.mta.subway.Trip;
import com.transit.service.MTABusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MTABusServiceImpl implements MTABusService {

    @Autowired
    private MTABusServiceDao busServiceDao;

    @Override
    public SiriResponse listTrips(BusListParams busListParams) {
        if (!busListParams.isStopSearch() && !busListParams.isRouteIdSearch() && !busListParams.isVehicleSearch()) {
            throw new IllegalArgumentException("Either a route id, stop id, and/or vehicle id must be specified to retrieve trips!");
        }
        if (busListParams.isDirectionSearch() && (busListParams.getDirection() != Trip.Direction.NORTH && busListParams.getDirection() != Trip.Direction.SOUTH)) {
            throw new IllegalArgumentException("Direction must be either NORTH or SOUTH!");
        }
        return busServiceDao.listTrips(busListParams);
    }

    @Override
    public List<Route> listRoutes(BusListParams busListParams) {
        return busServiceDao.listRoutes(busListParams);
    }

    @Override
    public StopResponse.Entry listStops(BusListParams busListParams) {
        if (!busListParams.isLocationSearch() && !busListParams.isRouteIdSearch()) {
            throw new IllegalArgumentException("Either a route id or geographic location must be specified to retrieve stops!");
        }
        return busServiceDao.listStops(busListParams);
    }
}
