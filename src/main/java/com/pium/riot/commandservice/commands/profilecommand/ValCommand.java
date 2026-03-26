package com.pium.riot.commandservice.commands.profilecommand;

import com.pium.riot.api.apiconfig.ApiHenrik;
import com.pium.riot.api.model.ValProfile;
import com.pium.riot.commandservice.commands.utils.ExpiringCache;
import com.pium.riot.commandservice.riotcommandmanagerservice.RiotBotCommand;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ValCommand implements RiotBotCommand {
    private ValProfileService service;
    private static final long CACHE_TTL = 10 * 60 * 1000;
    public static final ExpiringCache<byte[]> cardCache = new ExpiringCache<>(CACHE_TTL);
    public static final ExpiringCache<byte[]> historyCache = new ExpiringCache<>(CACHE_TTL);

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

        ApiHenrik apiHenrik = new ApiHenrik(summonerName, tag, region);
        service = new ValProfileService(apiHenrik);

        event.deferReply().queue();

        try {
            service.profilesBuilder(idMessage);
        } catch (IOException e) {
            event.getHook().sendMessage("No se encontró la cuenta").queue();
            return;
        }

        ValProfile profile = service.profileData.get(idMessage);
        if (profile != null) {
            byte[] profileCard = toBytes(profile.generateRankCard());

            byte[] historyCard = null;
            try {
                ValMatchHistoryService historyService = new ValMatchHistoryService(apiHenrik);
                historyCard = toBytes(historyService.buildMatchHistoryCard(profile));
            } catch (Exception ignored) {}

            byte[] finalHistoryCard = historyCard;
            event.getHook().sendFiles(
                FileUpload.fromData(new ByteArrayInputStream(profileCard), "card.png")
            ).addActionRow(
                Button.primary("val_profile", "Perfil").asDisabled(),
                Button.secondary("val_history", "Historial"),
                Button.link("https://github.com/AgustinJak/riot-discord-bot", "GitHub")
                    .withEmoji(Emoji.fromCustom("GitIcon", 1363525155573338212L, false)),
                Button.link("https://cafecito.app/aguspium", "Donaciones")
                    .withEmoji(Emoji.fromUnicode("☕"))
            ).queue(message -> {
                cardCache.put(message.getId(), profileCard);
                if (finalHistoryCard != null) {
                    historyCache.put(message.getId(), finalHistoryCard);
                }
            });
        } else {
            event.getHook().sendMessage("No se encontraron datos ranked de Valorant").queue();
        }

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
