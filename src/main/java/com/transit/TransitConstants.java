package com.transit;

import com.transit.domain.mta.Feed;

import java.util.*;
import java.util.stream.Collectors;

public class TransitConstants {
    public static Map<Integer, Feed> FEED_ID_MAP;
    public static Map<String, Feed> ROUTE_FEED_MAP;

    static {
        Map<Integer, Feed> tempFeedIdToFeed = new HashMap<>();
        tempFeedIdToFeed.put(1, new Feed(1, Arrays.asList("1", "2", "3", "4", "5", "6", "GS")));
        tempFeedIdToFeed.put(26, new Feed(26, Arrays.asList("A", "C", "E", "H", "FS")));
        tempFeedIdToFeed.put(16, new Feed(16, Arrays.asList("N", "Q", "R", "W")));
        tempFeedIdToFeed.put(21, new Feed(21, Arrays.asList("B", "D", "F", "M")));
        tempFeedIdToFeed.put(2, new Feed(2, Arrays.asList("L")));
        tempFeedIdToFeed.put(11, new Feed(11, Arrays.asList("G")));
        tempFeedIdToFeed.put(36, new Feed(36, Arrays.asList("J", "Z")));
        tempFeedIdToFeed.put(51, new Feed(51, Arrays.asList("7")));
        FEED_ID_MAP = Collections.unmodifiableMap(tempFeedIdToFeed);

        Map<String, Feed> tempLineToFeed = tempFeedIdToFeed.values()
                .stream()
                .flatMap(f -> f.getLines()
                        .stream()
                        .map(l -> new AbstractMap.SimpleEntry<>(l, f))
                        .collect(Collectors.toList())
                        .stream())
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        ROUTE_FEED_MAP = Collections.unmodifiableMap(tempLineToFeed);
    }

    public static final String LONG_MIN_VALUE_STRING = "" + Long.MIN_VALUE;
    public static final String DOUBLE_MIN_VALUE_STRING = "" + Double.MIN_VALUE;
}
