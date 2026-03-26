package com.pium.riot.commandservice.commands.utils;

import java.util.HashMap;
import java.util.Map;

public class ValMapImages {
    private ValMapImages() {}

    private static final String CDN = "https://media.valorant-api.com/maps/";
    private static final String SUFFIX = "/listviewicontall.png";

    private static final Map<String, String> MAP_IMAGES;
    static {
        MAP_IMAGES = new HashMap<>();
        MAP_IMAGES.put("Ascent", CDN + "7eaecc1b-4337-bbf6-6ab9-04b8f06b3319" + SUFFIX);
        MAP_IMAGES.put("Bind", CDN + "2c9d57ec-4431-9c5e-2939-8f9ef6dd5cba" + SUFFIX);
        MAP_IMAGES.put("Haven", CDN + "2bee0dc9-4ffe-519b-1cbd-7fbe763a6047" + SUFFIX);
        MAP_IMAGES.put("Split", CDN + "d960549e-485c-e861-8d71-aa9d1aed12a2" + SUFFIX);
        MAP_IMAGES.put("Icebox", CDN + "e2ad5c54-4114-a870-9641-8ea21279579a" + SUFFIX);
        MAP_IMAGES.put("Breeze", CDN + "2fb9a4fd-47b8-4e7d-a969-74b4046ebd53" + SUFFIX);
        MAP_IMAGES.put("Fracture", CDN + "b529448b-4d60-346e-e89e-00a4c527a405" + SUFFIX);
        MAP_IMAGES.put("Pearl", CDN + "fd267378-4d1d-484f-ff52-77821ed10dc2" + SUFFIX);
        MAP_IMAGES.put("Lotus", CDN + "2fe4ed3a-450a-948b-6d6b-e89a78e680a9" + SUFFIX);
        MAP_IMAGES.put("Sunset", CDN + "92584fbe-486a-b1b2-9faa-39b0f486b498" + SUFFIX);
        MAP_IMAGES.put("Abyss", CDN + "224b0a95-48b9-f703-1bd8-67aca101a61f" + SUFFIX);
    }

    public static String get(String mapName) {
        return MAP_IMAGES.get(mapName);
    }
}
