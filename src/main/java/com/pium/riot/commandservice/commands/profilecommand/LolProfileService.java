package com.pium.riot.commandservice.commands.profilecommand;

import com.pium.riot.api.apiconfig.ApiRiot;
import com.pium.riot.api.model.LolProfile;
import com.pium.riot.commandservice.commands.utils.EmbedConfigBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LolProfileService {
    public Map<String, ArrayList<MessageEmbed>> perfiles = new HashMap<>();
    public Map<String, ArrayList<LolProfile>> profileData = new HashMap<>();
    private final ApiRiot apiRiot;
    private String queue;

    public LolProfileService(ApiRiot ap) {
        apiRiot = ap;
    }

    public void profilesBuilder(String idMessage) throws IOException {
        ArrayList<MessageEmbed> embeds = new ArrayList<>();
        ArrayList<LolProfile> profiles = new ArrayList<>();

        for (int i = 0; i <= 1; i++) {
            LolProfile profile = apiRiot.getLolProfile(i);
            if (profile != null) {
                queue = profile.getQueueType().equals("RANKED_SOLO_5x5") ?
                        "Ranked Solo Q" : "Ranked Flex";
                embeds.add(profile.embedBuilder());
                profiles.add(profile);
            } else {
                embeds.add(defaultBuildEmbed());
                profiles.add(null);
            }
        }
        perfiles.put(idMessage, embeds);
        profileData.put(idMessage, profiles);
    }

    public MessageEmbed defaultBuildEmbed() {
        String a = queue == null ? "Server Wrong" :
                queue.equals("Ranked Solo Q") ? "Ranked Flex" : "Ranked Solo Q";

        return EmbedConfigBuilder.builder()
                .authorName(a)
                .title(a.equals("Server Wrong") ? " " : "Unranked")
                .color(Color.BLACK)
                .build()
                .buildEmbed();
    }

    public static String winrateCalculated(int wins, int losses) {
        int total = wins + losses;
        if (total == 0) return "0%";
        int winrate = (int) Math.round(((double) wins / total) * 100);
        return winrate + "%";
    }
}
