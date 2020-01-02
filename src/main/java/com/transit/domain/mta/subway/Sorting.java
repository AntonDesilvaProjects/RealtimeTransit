package com.transit.domain.mta.subway;

public final class Sorting {
    public enum Order {
        ASCENDING, DESCENDING;
        public static Order fromString(String string) {
            if (ASCENDING.name().equalsIgnoreCase(string) || "asc".equalsIgnoreCase(string)) {
                return ASCENDING;
            } else if (DESCENDING.name().equalsIgnoreCase(string) || "desc".equalsIgnoreCase(string)) {
                return DESCENDING;
            } else {
                throw new IllegalArgumentException(string + " is an invalid order string!");
            }
        }
    }
    public enum Field {
        ROUTE, DISTANCE, ARRIVAL_TIME, BOROUGH, STOP_NAME;
        public static Field fromString(String string) {
            for (Field f : Field.values()) {
                if (f.name().equalsIgnoreCase(string)) {
                    return f;
                }
            }
            throw new IllegalArgumentException(string + " is an invalid field string!");
        }
    }
}
