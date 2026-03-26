package com.pium.riot.api.apiconfig;

import com.pium.riot.api.config.UrlBuilder;
import com.pium.riot.api.model.LolProfile;
import com.pium.riot.api.model.TftProfile;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApiRiot {

    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String apiKey = dotenv.get("riot_api_key");
    private final String name;
    private final String tag;
    private final String puuid;
    private final String region;
    private final String server;

    public ApiRiot(String nick, String tag, String region) throws IOException {
        String[] regiones = region.split(":");
        this.region = regiones[0];
        this.server = regiones[1];
        this.name = nick;
        this.tag = tag.replaceAll("#", "");
        this.puuid = getPuuid();
    }

    private StringBuilder getConnection(String Url) throws IOException {
        URL url = URI.create(Url).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        if(connection.getResponseCode() != 200) {
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

    private String getPuuid() throws IOException {
    StringBuilder datos = getConnection(UrlBuilder.buildAccountUrl(name, tag, region, apiKey));
        JSONObject json = new JSONObject(datos.toString());
        return json.getString("puuid");

    }

    public TftProfile getTftProfile() throws IOException {
        StringBuilder response = getConnection(UrlBuilder.buildTftUrl(puuid, server, apiKey));
        JSONArray tft = new JSONArray(response.toString());

        if (tft.isEmpty()) {
            return null;
        }
        JSONObject queue = tft.getJSONObject(0);
        String riotUser = name + "#" + tag;

        return new TftProfile(
            riotUser,
            puuid,
            region,
            server,
            queue.getString("rank"),
            queue.getString("tier"),
            queue.getString("queueType"),
            queue.getInt("leaguePoints"),
            queue.getInt("wins"),
            queue.getInt("losses")
        );
    }

    public List<String> getMatchIds(int count) throws IOException {
        StringBuilder response = getConnection(UrlBuilder.buildMatchIdsUrl(puuid, region, count, apiKey));
        JSONArray ids = new JSONArray(response.toString());
        List<String> matchIds = new ArrayList<>();
        for (int i = 0; i < ids.length(); i++) {
            matchIds.add(ids.getString(i));
        }
        return matchIds;
    }

    public JSONObject getMatchDetail(String matchId) throws IOException {
        StringBuilder response = getConnection(UrlBuilder.buildMatchDetailUrl(matchId, region, apiKey));
        return new JSONObject(response.toString());
    }

    public String getPuuidValue() {
        return puuid;
    }

    public int getTopChampionId() throws IOException {
        StringBuilder response = getConnection(UrlBuilder.buildMasteryUrl(puuid, server, apiKey));
        JSONArray mastery = new JSONArray(response.toString());
        if (mastery.isEmpty()) return -1;
        return mastery.getJSONObject(0).getInt("championId");
    }

    public LolProfile getLolProfile(int cola) throws IOException {
        StringBuilder response = getConnection(UrlBuilder.buildLolUrl(puuid, server, apiKey));

        JSONArray lol = new JSONArray(response.toString());

        if (cola >= lol.length()) {
            return null;
        }
        JSONObject queue = lol.getJSONObject(cola);
        String riotUser = name + "#" + tag;

        return new LolProfile(
            riotUser,
            puuid,
            region,
            server,
            queue.getString("rank"),
            queue.getString("tier"),
            queue.getString("queueType"),
            queue.getInt("leaguePoints"),
            queue.getInt("wins"),
            queue.getInt("losses")
        );
    }
}