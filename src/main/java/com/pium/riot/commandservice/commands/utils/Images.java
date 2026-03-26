package com.pium.riot.commandservice.commands.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Images {
    private Images() {
    }

    public static final String IRON = "IRON";
    public static final String BRONZE = "BRONZE";
    public static final String SILVER = "SILVER";
    public static final String GOLD = "GOLD";
    public static final String PLATINUM = "PLATINUM";
    public static final String EMERALD = "EMERALD";
    public static final String DIAMOND = "DIAMOND";
    public static final String MASTER = "MASTER";
    public static final String GRANDMASTER = "GRANDMASTER";
    public static final String CHALLENGER = "CHALLENGER";
    private static final Map<String, String> IMAGES_API_VALUES;
    static {
        IMAGES_API_VALUES = new HashMap<>();
        IMAGES_API_VALUES.put(IRON, "https://i.imgur.com/7OZ4oYH.png");
        IMAGES_API_VALUES.put(BRONZE, "https://i.imgur.com/6jkJr9p.png");
        IMAGES_API_VALUES.put(SILVER, "https://i.imgur.com/uWrOGrz.png");
        IMAGES_API_VALUES.put(GOLD, "https://i.imgur.com/jgv9XjL.png");
        IMAGES_API_VALUES.put(PLATINUM, "https://i.imgur.com/uZqfpNG.png");
        IMAGES_API_VALUES.put(EMERALD, "https://i.imgur.com/YJTosSv.png");
        IMAGES_API_VALUES.put(DIAMOND, "https://i.imgur.com/1L3tYnb.png");
        IMAGES_API_VALUES.put(MASTER, "https://i.imgur.com/4JJdusj.png");
        IMAGES_API_VALUES.put(GRANDMASTER, "https://i.imgur.com/sYvXKjB.png");
        IMAGES_API_VALUES.put(CHALLENGER, "https://i.imgur.com/xtYfCfQ.png");
    }

    public static String getApiValue(String imagesName) {
        return IMAGES_API_VALUES.get(imagesName);
    }

    public static boolean isValid(String imagesName) {
        return IMAGES_API_VALUES.containsKey(imagesName.toUpperCase());
    }

    public static Set<String> getAvailableImages() {
        return IMAGES_API_VALUES.keySet();
    }

    public static Map<String, String> getImagesMap() {
        return IMAGES_API_VALUES;
    }
}
