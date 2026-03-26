package com.pium.riot.api.model;

import com.pium.riot.commandservice.commands.utils.EmbedColor;
import com.pium.riot.commandservice.commands.utils.EmbedConfigBuilder;
import com.pium.riot.commandservice.commands.utils.Images;
import com.pium.riot.commandservice.commands.utils.ProgressBar;
import com.pium.riot.commandservice.commands.utils.RankCardGenerator;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;
import java.io.InputStream;

@Getter
@Setter
public class TftProfile extends RiotProfile {
    private String rank;
    private String tier;
    private String queueType;
    private Integer leaguePoints;
    private Integer wins;
    private Integer losses;

    public TftProfile(String riotUser,
                      String puuid,
                      String region,
                      String server,
                      String rank,
                      String tier,
                      String queueType,
                      Integer leaguePoints,
                      Integer wins,
                      Integer losses) {
        super(riotUser, puuid, region, server);
        this.rank = rank;
        this.tier = tier;
        this.queueType = queueType;
        this.leaguePoints = leaguePoints;
        this.wins = wins;
        this.losses = losses;
    }

    private static final String SPLASH_URL = "https://pbs.twimg.com/media/EiFryAbWsAEp-hS?format=jpg&name=4096x4096";

    public InputStream generateRankCard() throws IOException {
        return RankCardGenerator.generate(
                riotUser, tier, rank, leaguePoints,
                wins, losses, "Ranked TFT",
                EmbedColor.getApiValue(tier),
                Images.getApiValue(tier),
                SPLASH_URL
        );
    }

    public MessageEmbed embedBuilder() {
        int totalGames = wins + losses;
        return EmbedConfigBuilder.builder()
                .authorName("Ranked TFT")
                .title(tier + " " + rank)
                .description("LP: " + leaguePoints)
                .field("Games: ")
                .inField("Total: " + totalGames + "\n" + "Wins: " + wins + " | Losses: " + losses)
                .secondField("Winrate: ")
                .secondInField(ProgressBar.build(wins, totalGames))
                .footer(riotUser)
                .color(EmbedColor.getApiValue(tier))
                .thumbnailUrlImage(Images.getApiValue(tier))
                .build()
                .buildEmbed();
    }
}
