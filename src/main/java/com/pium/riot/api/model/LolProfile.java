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
public class LolProfile extends RiotProfile{
    private String rank;
    private String tier;
    private String queueType;
    private Integer leaguePoints;
    private Integer wins;
    private Integer losses;

    public LolProfile(String riotUser,
                      String puuid,
                      String region,
                      String server,
                      String rank,
                      String tier,
                      String queueType,
                      Integer leaguePoints,
                      Integer wins,
                      Integer losses
    ){

        super(riotUser, puuid, region, server);

        this.rank = rank;
        this.tier = tier;
        this.queueType = queueType;
        this.leaguePoints = leaguePoints;
        this.wins = wins;
        this.losses = losses;
    }

    public MessageEmbed embedBuilder() {
        int totalGames = wins + losses;
        return EmbedConfigBuilder.builder()
                .authorName(queueType.equals("RANKED_FLEX_SR") ? "Ranked FLEX" : "Ranked SoloQ")
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

    public InputStream generateRankCard() throws IOException {
        return generateRankCard(null);
    }

    public InputStream generateRankCard(String splashUrl) throws IOException {
        String queueName = queueType.equals("RANKED_FLEX_SR") ? "Ranked FLEX" : "Ranked SoloQ";
        return RankCardGenerator.generate(
                riotUser, tier, rank, leaguePoints,
                wins, losses, queueName,
                EmbedColor.getApiValue(tier),
                Images.getApiValue(tier),
                splashUrl
        );
    }

    public String showUserStats() {
        return String.format(
                "Ranked: %s\nRiotUser: %s\nElo: %s %s\nLP: %d\nWins: %d\nLosses: %d",
                getRiotUser(),
                getQueueType(),
                getTier(),
                getRank(),
                getLeaguePoints(),
                getWins(),
                getLosses()
        );
    }
}