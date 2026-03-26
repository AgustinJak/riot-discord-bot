package com.pium.riot.commandservice.commands.profilecommand;

import com.pium.riot.api.apiconfig.ApiRiot;
import com.pium.riot.api.model.TftProfile;
import com.pium.riot.commandservice.riotcommandmanagerservice.RiotBotCommand;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TftCommand implements RiotBotCommand {
    private TftProfileService service;

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

        try {
            service = new TftProfileService(new ApiRiot(summonerName, tag, region));
        } catch (IOException e) {
            event.reply("No existe la cuenta").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        try {
            service.profilesBuilder(idMessage);
        } catch (IOException e) {
            event.getHook().sendMessage("Error al obtener el perfil de TFT").queue();
            return;
        }

        TftProfile profile = service.profileData.get(idMessage);
        if (profile != null) {
            byte[] card = toBytes(profile.generateRankCard());
            event.getHook().sendFiles(
                FileUpload.fromData(new ByteArrayInputStream(card), "rank_card.png")
            ).addActionRow(
                Button.link("https://github.com/AgustinJak/riot-discord-bot", "GitHub")
                    .withEmoji(Emoji.fromCustom("GitIcon", 1363525155573338212L, false)),
                Button.link("https://cafecito.app/aguspium", "Donaciones")
                    .withEmoji(Emoji.fromUnicode("☕"))
            ).queue();
        } else {
            event.getHook().sendMessage("No se encontraron datos ranked de TFT").queue();
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
