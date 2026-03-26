package com.pium.riot.botservice.config.utils;

import java.util.Map;
import java.util.Set;

public final class ValRegion {

    private ValRegion() {
    }

    public static final String LAS = "LAS";
    public static final String LAN = "LAN";
    public static final String BR = "BR";
    public static final String NA = "NA";
    public static final String EUW = "EUW";
    public static final String KR = "KR";
    public static final String JP = "JP";
    public static final String SEA = "SEA";

    private static final Map<String, String> REGION_API_VALUES = Map.of(
            LAS, "latam",
            LAN, "latam",
            BR, "br",
            NA, "na",
            EUW, "eu",
            KR, "kr",
            JP, "ap",
            SEA, "ap"
    );

    public static String getApiValue(String regionName) {
        return REGION_API_VALUES.get(regionName.toUpperCase());
    }

    public static Set<String> getAvailableRegions() {
        return REGION_API_VALUES.keySet();
    }

    public static Map<String, String> getRegionMap() {
        return REGION_API_VALUES;
    }
}
