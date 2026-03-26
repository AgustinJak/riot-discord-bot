package com.pium.riot.commandservice.commands.utils;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DataDragon {

    private static final String BASE_URL = "https://ddragon.leagueoflegends.com/cdn/16.6.1";
    private static Map<Integer, String> championMap;

    public static String getChampionIcon(String championName) {
        return BASE_URL + "/img/champion/" + championName + ".png";
    }

    public static String getProfileIcon(int iconId) {
        return BASE_URL + "/img/profileicon/" + iconId + ".png";
    }

    public static String getChampionSplash(String championName) {
        return "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/" + championName + "_0.jpg";
    }

    public static String getChampionNameById(int championId) {
        if (championMap == null) {
            loadChampionMap();
        }
        return championMap.getOrDefault(championId, null);
    }

    private static void loadChampionMap() {
        championMap = new HashMap<>();
        try {
            Scanner sc = new Scanner(URI.create(BASE_URL + "/data/en_US/champion.json").toURL().openStream());
            StringBuilder sb = new StringBuilder();
            while (sc.hasNext()) sb.append(sc.nextLine());
            sc.close();

            JSONObject data = new JSONObject(sb.toString()).getJSONObject("data");
            for (String name : data.keySet()) {
                JSONObject champ = data.getJSONObject(name);
                int key = Integer.parseInt(champ.getString("key"));
                championMap.put(key, champ.getString("id"));
            }
        } catch (IOException ignored) {
        }
    }
}
