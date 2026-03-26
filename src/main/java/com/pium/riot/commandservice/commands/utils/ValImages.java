package com.pium.riot.commandservice.commands.utils;

import java.util.HashMap;
import java.util.Map;

public class ValImages {
    private ValImages() {
    }

    private static final Map<String, String> IMAGES_API_VALUES;
    static {
        IMAGES_API_VALUES = new HashMap<>();
        IMAGES_API_VALUES.put("IRON", "https://i.imgur.com/MPJlYlY.png");
        IMAGES_API_VALUES.put("BRONZE", "https://i.imgur.com/m2T6bfn.png");
        IMAGES_API_VALUES.put("SILVER", "https://i.imgur.com/tzTho8F.png");
        IMAGES_API_VALUES.put("GOLD", "https://i.imgur.com/HiCicdC.png");
        IMAGES_API_VALUES.put("PLATINUM", "https://i.imgur.com/7TzVxEH.png");
        IMAGES_API_VALUES.put("DIAMOND", "https://i.imgur.com/9Mp245d.png");
        IMAGES_API_VALUES.put("ASCENDANT", "https://i.imgur.com/ePCKdn7.png");
        IMAGES_API_VALUES.put("IMMORTAL", "https://i.imgur.com/DoAsaD9.png");
        IMAGES_API_VALUES.put("RADIANT", "https://i.imgur.com/Atp9bRp.png");
    }

    public static String getApiValue(String tierName) {
        return IMAGES_API_VALUES.get(tierName);
    }
}
