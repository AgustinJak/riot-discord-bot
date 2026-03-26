package com.pium.riot.commandservice.commands.profilecommand;

import com.pium.riot.api.apiconfig.ApiHenrik;
import com.pium.riot.api.model.ValProfile;
import com.pium.riot.commandservice.commands.utils.EmbedColor;
import com.pium.riot.commandservice.commands.utils.ValImages;
import com.pium.riot.commandservice.commands.utils.ValAgentImages;
import com.pium.riot.commandservice.commands.utils.ValMapImages;
import com.pium.riot.commandservice.commands.utils.ValMatchHistoryCardGenerator;
import com.pium.riot.commandservice.commands.utils.ValMatchHistoryCardGenerator.MatchRow;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ValMatchHistoryService {
    private final ApiHenrik apiHenrik;

    public ValMatchHistoryService(ApiHenrik apiHenrik) {
        this.apiHenrik = apiHenrik;
    }

    public InputStream buildMatchHistoryCard(ValProfile profile) throws IOException {
        List<JSONObject> matches = apiHenrik.getMatchHistory(5);
        String riotUser = apiHenrik.getRiotUser();

        List<MatchRow> rows = new ArrayList<>();

        for (JSONObject match : matches) {
            JSONObject metadata = match.getJSONObject("metadata");
            JSONArray players = match.getJSONArray("players");
            JSONArray teams = match.getJSONArray("teams");

            JSONObject player = findPlayer(players, riotUser);
            if (player == null) continue;

            String teamId = player.getString("team_id");
            JSONObject agent = player.getJSONObject("agent");
            String agentName = agent.getString("name");
            String agentIcon = ValAgentImages.get(agentName);

            JSONObject stats = player.getJSONObject("stats");
            int kills = stats.getInt("kills");
            int deaths = stats.getInt("deaths");
            int assists = stats.getInt("assists");
            int score = stats.getInt("score");

            String mapName = metadata.getJSONObject("map").getString("name");
            String mapImageUrl = ValMapImages.get(mapName);
            int totalRounds = 0;

            boolean won = false;
            int roundsWon = 0;
            int roundsLost = 0;
            for (int i = 0; i < teams.length(); i++) {
                JSONObject team = teams.getJSONObject(i);
                if (team.getString("team_id").equals(teamId)) {
                    won = team.getBoolean("won");
                    JSONObject rounds = team.getJSONObject("rounds");
                    roundsWon = rounds.getInt("won");
                    roundsLost = rounds.getInt("lost");
                    totalRounds = roundsWon + roundsLost;
                    break;
                }
            }

            int acs = totalRounds > 0 ? score / totalRounds : score;

            boolean isMvp = won && isTopScorer(players, player);

            rows.add(new MatchRow(agentIcon, agentName, kills, deaths, assists,
                    acs, mapName, mapImageUrl, won, roundsWon, roundsLost, isMvp));
        }

        String tierKey = extractTier(profile.getCurrentRank());
        Color tierColor = EmbedColor.getApiValue(tierKey);
        String rankIconUrl = ValImages.getApiValue(tierKey);

        return ValMatchHistoryCardGenerator.generate(
                profile.getCurrentRank(),
                profile.getRr(),
                rankIconUrl != null ? rankIconUrl : "",
                tierColor,
                rows
        );
    }

    private boolean isTopScorer(JSONArray players, JSONObject currentPlayer) {
        String teamId = currentPlayer.getString("team_id");
        int myScore = currentPlayer.getJSONObject("stats").getInt("score");

        for (int i = 0; i < players.length(); i++) {
            JSONObject p = players.getJSONObject(i);
            if (!p.getString("team_id").equals(teamId)) continue;
            if (p.getJSONObject("stats").getInt("score") > myScore) return false;
        }
        return true;
    }

    private JSONObject findPlayer(JSONArray players, String riotUser) {
        String[] parts = riotUser.split("#");
        String name = parts[0];
        String tag = parts.length > 1 ? parts[1] : "";

        for (int i = 0; i < players.length(); i++) {
            JSONObject p = players.getJSONObject(i);
            if (p.getString("name").equalsIgnoreCase(name) &&
                p.getString("tag").equalsIgnoreCase(tag)) {
                return p;
            }
        }
        return null;
    }

    private String extractTier(String rank) {
        if (rank == null || rank.isEmpty()) return "";
        return rank.split(" ")[0].toUpperCase();
    }
}
