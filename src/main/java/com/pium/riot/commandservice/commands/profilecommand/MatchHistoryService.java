package com.pium.riot.commandservice.commands.profilecommand;

import com.pium.riot.api.apiconfig.ApiRiot;
import com.pium.riot.commandservice.commands.utils.DataDragon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class MatchHistoryService {
    private final ApiRiot apiRiot;
    private static final Color WIN_COLOR = new Color(40, 167, 69);
    private static final Color LOSS_COLOR = new Color(220, 53, 69);
    private static final Color MIXED_COLOR = new Color(50, 50, 60);

    public MatchHistoryService(ApiRiot apiRiot) {
        this.apiRiot = apiRiot;
    }

    public MessageEmbed buildMatchHistoryEmbed(String riotUser) throws IOException {
        List<String> matchIds = apiRiot.getMatchIds(5);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Historial de Partidas");
        eb.setFooter(riotUser);

        if (matchIds.isEmpty()) {
            eb.setDescription("No se encontraron partidas recientes");
            eb.setColor(MIXED_COLOR);
            return eb.build();
        }

        String puuid = apiRiot.getPuuidValue();
        int wins = 0;
        int total = 0;
        String lastChampion = null;

        for (String matchId : matchIds) {
            JSONObject match = apiRiot.getMatchDetail(matchId);
            JSONObject info = match.getJSONObject("info");
            JSONObject player = findPlayer(info, puuid);
            if (player == null) continue;

            String champion = player.getString("championName");
            int kills = player.getInt("kills");
            int deaths = player.getInt("deaths");
            int assists = player.getInt("assists");
            int cs = player.getInt("totalMinionsKilled") + player.getInt("neutralMinionsKilled");
            boolean win = player.getBoolean("win");
            int duration = info.getInt("gameDuration");
            long gameEnd = info.getLong("gameEndTimestamp") / 1000;
            int damage = player.getInt("totalDamageDealtToChampions");

            if (lastChampion == null) lastChampion = champion;
            if (win) wins++;
            total++;

            String result = win ? "Victoria" : "Derrota";
            String kda = kills + "/" + deaths + "/" + assists;
            double kdaRatio = deaths == 0 ? (kills + assists) : (double)(kills + assists) / deaths;
            String kdaFormatted = String.format("%.1f", kdaRatio);
            String timestamp = "<t:" + gameEnd + ":R>";

            eb.addField(
                    (win ? "\u2705 " : "\u274C ") + champion + " — " + result,
                    kda + " KDA (" + kdaFormatted + ") | " + cs + " CS | " +
                    formatDamage(damage) + " DMG\n" +
                    formatDuration(duration) + " | " + timestamp,
                    false
            );
        }

        if (total > 0 && lastChampion != null) {
            eb.setThumbnail(DataDragon.getChampionIcon(lastChampion));
        }

        if (wins == total) {
            eb.setColor(WIN_COLOR);
        } else if (wins == 0) {
            eb.setColor(LOSS_COLOR);
        } else {
            eb.setColor(MIXED_COLOR);
        }

        return eb.build();
    }

    private JSONObject findPlayer(JSONObject info, String puuid) {
        var participants = info.getJSONArray("participants");
        for (int i = 0; i < participants.length(); i++) {
            JSONObject p = participants.getJSONObject(i);
            if (p.getString("puuid").equals(puuid)) {
                return p;
            }
        }
        return null;
    }

    private String formatDuration(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return min + ":" + String.format("%02d", sec);
    }

    private String formatDamage(int damage) {
        if (damage >= 1000) {
            return String.format("%.1fk", damage / 1000.0);
        }
        return String.valueOf(damage);
    }
}
