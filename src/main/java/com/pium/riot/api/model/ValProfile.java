package com.pium.riot.api.model;

import com.pium.riot.commandservice.commands.utils.EmbedColor;
import com.pium.riot.commandservice.commands.utils.EmbedConfigBuilder;
import com.pium.riot.commandservice.commands.utils.ProgressBar;
import com.pium.riot.commandservice.commands.utils.RankCardGenerator;
import com.pium.riot.commandservice.commands.utils.ValImages;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

@Getter
@Setter
public class ValProfile {
    private String riotUser;
    private String region;
    private String currentRank;
    private Integer rr;
    private Integer elo;
    private String peakRank;
    private Integer wins;
    private Integer games;

    public ValProfile(String riotUser,
                      String region,
                      String currentRank,
                      Integer rr,
                      Integer elo,
                      String peakRank,
                      Integer wins,
                      Integer games) {
        this.riotUser = riotUser;
        this.region = region;
        this.currentRank = currentRank;
        this.rr = rr;
        this.elo = elo;
        this.peakRank = peakRank;
        this.wins = wins;
        this.games = games;
    }

    private static final String SPLASH_URL = "https://i.imgur.com/2U6wLeD.png";

    public InputStream generateRankCard() throws IOException {
        String tierKey = extractTier(currentRank);
        String rankNum = currentRank.contains(" ") ? currentRank.split(" ")[1] : "";
        int losses = games - wins;
        return RankCardGenerator.generate(
                riotUser, tierKey, rankNum, rr,
                wins, losses, "Valorant Competitive",
                EmbedColor.getApiValue(tierKey),
                ValImages.getApiValue(tierKey),
                SPLASH_URL
        );
    }

    public MessageEmbed embedBuilder() {
        int losses = games - wins;
        String tierKey = extractTier(currentRank);
        Color color = EmbedColor.getApiValue(tierKey);
        String image = ValImages.getApiValue(tierKey);

        return EmbedConfigBuilder.builder()
                .authorName("Valorant Competitive")
                .title(currentRank)
                .description("LP: " + rr + " | Peak: " + peakRank)
                .field("Games: ")
                .inField("Total: " + games + "\n" + "Wins: " + wins + " | Losses: " + losses)
                .secondField("Winrate: ")
                .secondInField(ProgressBar.build(wins, games))
                .footer(riotUser)
                .color(color != null ? color : Color.GRAY)
                .thumbnailUrlImage(image)
                .build()
                .buildEmbed();
    }

    private String extractTier(String rank) {
        if (rank == null || rank.isEmpty()) return "";
        String[] parts = rank.split(" ");
        return parts[0].toUpperCase();
    }
}
