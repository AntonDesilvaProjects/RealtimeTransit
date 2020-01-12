package com.transit.service;

import com.transit.domain.mta.bus.BusListParams;
import com.transit.domain.mta.bus.Route;
import com.transit.domain.mta.bus.SiriResponse;
import com.transit.domain.mta.bus.StopResponse;

import java.util.List;

public interface MTABusService {

    SiriResponse listTrips(BusListParams listParams);

    List<Route> listRoutes(BusListParams busListParams);

    StopResponse.Entry listStops(BusListParams busListParams);
}
