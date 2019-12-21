package com.transit.cache;

import com.google.common.cache.CacheLoader;
import com.transit.dao.MTASubwayGTFSDao;
import com.transit.domain.mta.SubwayStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MTASubwayStationCacheLoader extends CacheLoader<Integer, SubwayStation> {

    @Autowired
    private MTASubwayGTFSDao mtaSubwayGTFSDao;

    @Override
    public SubwayStation load(Integer integer) throws Exception {
        return null;
    }
}
