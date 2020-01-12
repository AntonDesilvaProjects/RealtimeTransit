package com.transit.dao;

import com.transit.domain.mta.bus.BusListParams;
import com.transit.domain.mta.bus.Route;
import com.transit.domain.mta.bus.SiriResponse;
import com.transit.domain.mta.bus.StopResponse;

import java.util.List;

public interface MTABusServiceDao {

    List<Route> listRoutes(BusListParams busListParams);

    StopResponse.Entry listStops(BusListParams busListParams);

    SiriResponse listTrips(BusListParams busListParams);
}
