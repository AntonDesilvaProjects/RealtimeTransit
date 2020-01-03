package com.transit.service.impl;

import com.transit.dao.MTABusServiceDao;
import com.transit.domain.mta.bus.BusListParams;
import com.transit.domain.mta.bus.Route;
import com.transit.domain.mta.bus.Stop;
import com.transit.service.MTABusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MTABusServiceImpl implements MTABusService {

    @Autowired
    private MTABusServiceDao busServiceDao;

    @Override
    public List<Route> listRoutes(BusListParams busListParams) {
        return busServiceDao.listRoutes(busListParams);
    }

    @Override
    public List<Stop> listStops(BusListParams busListParams) {
        if (!busListParams.isLocationSearch() && !busListParams.isRouteIdSearch()) {
            throw new IllegalArgumentException("Either a route id or geographic location must be specified to retrieve stops!");
        }
        return busServiceDao.listStops(busListParams);
    }
}
