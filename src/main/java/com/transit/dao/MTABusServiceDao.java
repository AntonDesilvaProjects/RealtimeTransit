package com.transit.dao;

import com.transit.domain.mta.bus.BusListParams;
import com.transit.domain.mta.bus.Route;
import com.transit.domain.mta.bus.Stop;

import java.util.List;

public interface MTABusServiceDao {
    List<Route> listRoutes(BusListParams busListParams);

    List<Stop> listStops(BusListParams busListParams);
}
