package com.pium.riot.api.config;

public class UrlBuilder {

    public static String buildAccountUrl(String name, String tag, String region, String apiKey) {
        name = name.replaceAll(" ", "%20");
        return String.format("https://%s.api.riotgames.com/riot/account/v1/accounts/by-riot-id/%s/%s?api_key=%s",
                region, name, tag, apiKey);
    }

    public static String buildTftUrl(String puuid, String server, String apiKey) {
        return String.format("https://%s.api.riotgames.com/tft/league/v1/by-puuid/%s?api_key=%s",
                server, puuid, apiKey);
    }

    public static String buildMatchIdsUrl(String puuid, String region, int count, String apiKey) {
        return String.format("https://%s.api.riotgames.com/lol/match/v5/matches/by-puuid/%s/ids?start=0&count=%d&api_key=%s",
                region, puuid, count, apiKey);
    }

    public static String buildMatchDetailUrl(String matchId, String region, String apiKey) {
        return String.format("https://%s.api.riotgames.com/lol/match/v5/matches/%s?api_key=%s",
                region, matchId, apiKey);
    }

    public static String buildLolUrl(String puuid, String server, String apiKey) {
        return String.format("https://%s.api.riotgames.com/lol/league/v4/entries/by-puuid/%s?api_key=%s",
                server, puuid, apiKey);
    }

    public static String buildMasteryUrl(String puuid, String server, String apiKey) {
        return String.format("https://%s.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-puuid/%s/top?count=1&api_key=%s",
                server, puuid, apiKey);
    }
}
