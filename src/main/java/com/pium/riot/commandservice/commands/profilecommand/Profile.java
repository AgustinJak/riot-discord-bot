package com.pium.riot.commandservice.commands.profilecommand;

import com.pium.riot.api.apiconfig.ApiRiot;
import com.pium.riot.api.model.LolProfile;
import com.pium.riot.commandservice.commands.utils.DataDragon;
import com.pium.riot.commandservice.commands.utils.ExpiringCache;
import com.pium.riot.commandservice.riotcommandmanagerservice.RiotBotCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Profile implements RiotBotCommand {
    private LolProfileService service;
    private static final long CACHE_TTL = 10 * 60 * 1000;
    public static final ExpiringCache<ArrayList<byte[]>> cardCache = new ExpiringCache<>(CACHE_TTL);
    public static final ExpiringCache<String> userCache = new ExpiringCache<>(CACHE_TTL);
    public static final ExpiringCache<MessageEmbed> historyCache = new ExpiringCache<>(CACHE_TTL);

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        String summonerName = getOptionValue(event, "nick");
        String tag = getOptionValue(event, "tag");
        String region = getOptionValue(event, "region");
        String idMessage = event.getMessageChannel().getId();

        if (summonerName == null || tag == null || region == null) {
            event.reply("Faltan argumentos.").setEphemeral(true).queue();
            return;
        }

        ApiRiot apiRiot;
        try {
            apiRiot = new ApiRiot(summonerName, tag, region);
            service = new LolProfileService(apiRiot);
        } catch (IOException e) {
            event.reply("No existe la cuenta").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        try {
            service.profilesBuilder(idMessage);
        } catch (IOException e) {
            event.getHook().sendMessage("Error al obtener el perfil").queue();
            return;
        }

        String splashUrl = null;
        try {
            int champId = apiRiot.getTopChampionId();
            if (champId > 0) {
                String champName = DataDragon.getChampionNameById(champId);
                if (champName != null) {
                    splashUrl = DataDragon.getChampionSplash(champName);
                }
            }
        } catch (IOException ignored) {
        }

        ArrayList<LolProfile> profiles = service.profileData.get(idMessage);
        ArrayList<byte[]> cards = new ArrayList<>();
        for (LolProfile profile : profiles) {
            if (profile != null) {
                cards.add(toBytes(profile.generateRankCard(splashUrl)));
            } else {
                cards.add(null);
            }
        }

        MessageEmbed historyEmbed = null;
        try {
            MatchHistoryService historyService = new MatchHistoryService(apiRiot);
            historyEmbed = historyService.buildMatchHistoryEmbed(summonerName + "#" + tag);
        } catch (Exception ignored) {
        }

        byte[] soloqCard = cards.get(0);
        if (soloqCard == null && cards.size() > 1) soloqCard = cards.get(1);

        if (soloqCard != null) {
            MessageEmbed finalHistoryEmbed = historyEmbed;
            event.getHook().sendFiles(
                FileUpload.fromData(new ByteArrayInputStream(soloqCard), "rank_card.png")
            ).addActionRow(
                Button.primary("lol_soloq", "Solo Q").asDisabled(),
                Button.secondary("lol_flex", "Flex"),
                Button.secondary("lol_history", "Historial"),
                Button.link("https://github.com/AgustinJak/riot-discord-bot", "GitHub")
                    .withEmoji(Emoji.fromCustom("GitIcon", 1363525155573338212L, false))
            ).queue(message -> {
                cardCache.put(message.getId(), cards);
                userCache.put(message.getId(), summonerName + "#" + tag);
                if (finalHistoryEmbed != null) {
                    historyCache.put(message.getId(), finalHistoryEmbed);
                }
            });
        } else {
            event.getHook().sendMessage("No se encontraron datos ranked").queue();
        }

        service.perfiles.remove(idMessage);
        service.profileData.remove(idMessage);
    }

    private byte[] toBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        is.transferTo(baos);
        return baos.toByteArray();
    }

    private String getOptionValue(SlashCommandInteractionEvent event, String optionName) {
        return event.getOption(optionName) != null ? event.getOption(optionName).getAsString() : null;
    }
}
