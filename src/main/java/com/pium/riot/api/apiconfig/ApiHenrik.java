package com.pium.riot.api.apiconfig;

import com.pium.riot.api.model.ValProfile;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class ApiHenrik {

    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String apiKey = dotenv.get("henrik_api_key");
    private final String name;
    private final String tag;
    private final String region;

    public ApiHenrik(String name, String tag, String region) {
        this.name = name;
        this.tag = tag.replaceAll("#", "");
        this.region = region;
    }

    private StringBuilder getConnection(String requestUrl) throws IOException {
        URL url = URI.create(requestUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", apiKey);
        if (connection.getResponseCode() != 200) {
            throw new IOException("Error: " + connection.getResponseCode());
        }
        connection.connect();

        StringBuilder response = new StringBuilder();
        Scanner sc = new Scanner(connection.getInputStream());
        while (sc.hasNext()) {
            response.append(sc.nextLine());
        }
        sc.close();

        return response;
    }

    public ValProfile getValProfile() throws IOException {
        String encodedName = name.replaceAll(" ", "%20");
        String requestUrl = String.format(
                "https://api.henrikdev.xyz/valorant/v3/mmr/%s/pc/%s/%s",
                region, encodedName, tag);

        StringBuilder response = getConnection(requestUrl);
        JSONObject json = new JSONObject(response.toString());
        JSONObject data = json.getJSONObject("data");

        JSONObject current = data.getJSONObject("current");
        String currentRank = current.getJSONObject("tier").getString("name");
        int rr = current.getInt("rr");
        int elo = current.getInt("elo");

        String peakRank = data.getJSONObject("peak").getJSONObject("tier").getString("name");

        int wins = 0;
        int games = 0;
        JSONArray seasonal = data.getJSONArray("seasonal");
        if (!seasonal.isEmpty()) {
            JSONObject lastSeason = seasonal.getJSONObject(seasonal.length() - 1);
            wins = lastSeason.getInt("wins");
            games = lastSeason.getInt("games");
        }

        String riotUser = name + "#" + tag;

        return new ValProfile(
                riotUser, region,
                currentRank, rr, elo, peakRank,
                wins, games
        );
    }

    public List<JSONObject> getMatchHistory(int count) throws IOException {
        String encodedName = name.replaceAll(" ", "%20");
        String requestUrl = String.format(
                "https://api.henrikdev.xyz/valorant/v4/matches/%s/pc/%s/%s?mode=competitive&size=%d",
                region, encodedName, tag, count);

        StringBuilder response = getConnection(requestUrl);
        JSONObject json = new JSONObject(response.toString());
        JSONArray data = json.getJSONArray("data");

        List<JSONObject> matches = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            matches.add(data.getJSONObject(i));
        }
        return matches;
    }

    public String getRiotUser() {
        return name + "#" + tag;
    }
}
