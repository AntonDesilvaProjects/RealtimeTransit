package com.transit;

import com.transit.domain.mta.Feed;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TransitConstants {
    public  static Map<Integer, Feed> FEED_ID_MAP;
    static {
        Map<Integer, Feed> tempFeeds = new HashMap<>();
        tempFeeds.put(1, new Feed(1, Arrays.asList("1", "2", "3", "4", "5", "6", "GS")));
        tempFeeds.put(26, new Feed(26, Arrays.asList("A", "C", "E", "H", "FS")));
        tempFeeds.put(16, new Feed(16, Arrays.asList("N", "Q", "R", "W")));
        tempFeeds.put(21, new Feed(21, Arrays.asList("B", "D", "F", "M")));
        tempFeeds.put(2, new Feed(2, Arrays.asList("L")));
        tempFeeds.put(11, new Feed(11, Arrays.asList("G")));
        tempFeeds.put(36, new Feed(36, Arrays.asList("J", "Z")));
        tempFeeds.put(51, new Feed(51, Arrays.asList("7")));
        FEED_ID_MAP = Collections.unmodifiableMap(tempFeeds);
    }
}
