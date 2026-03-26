package com.pium.riot.commandservice.commands.utils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EmbedColor {
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
    public static final String ASCENDANT = "ASCENDANT";
    public static final String IMMORTAL = "IMMORTAL";
    public static final String RADIANT = "RADIANT";

    private static final Map<String, Color> COLOR_API_VALUES;
    static {
        COLOR_API_VALUES = new HashMap<>();
        COLOR_API_VALUES.put(IRON, Color.DARK_GRAY);
        COLOR_API_VALUES.put(BRONZE, new Color(150, 75, 0));
        COLOR_API_VALUES.put(SILVER, Color.GRAY);
        COLOR_API_VALUES.put(GOLD, Color.ORANGE);
        COLOR_API_VALUES.put(PLATINUM, Color.CYAN);
        COLOR_API_VALUES.put(EMERALD, Color.GREEN);
        COLOR_API_VALUES.put(DIAMOND, Color.BLUE);
        COLOR_API_VALUES.put(MASTER, Color.MAGENTA);
        COLOR_API_VALUES.put(GRANDMASTER, Color.RED);
        COLOR_API_VALUES.put(CHALLENGER, Color.YELLOW);
        COLOR_API_VALUES.put(ASCENDANT, new Color(0, 255, 127));
        COLOR_API_VALUES.put(IMMORTAL, new Color(220, 20, 60));
        COLOR_API_VALUES.put(RADIANT, new Color(255, 255, 224));
    }

    public static Color getApiValue(String colorName) {
        return COLOR_API_VALUES.get(colorName);
    }

    public static boolean isValid(String colorName) {
        return COLOR_API_VALUES.containsKey(colorName.toUpperCase());
    }

    public static Set<String> getAvailableImages() {
        return COLOR_API_VALUES.keySet();
    }

    public static Map<String, Color> getColorMap() {
        return COLOR_API_VALUES;
    }
}
