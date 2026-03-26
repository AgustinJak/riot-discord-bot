package com.pium.riot.commandservice.riotcommandmanagerservice;

import com.pium.riot.commandservice.commands.profilecommand.Profile;
import com.pium.riot.commandservice.commands.profilecommand.TftCommand;
import com.pium.riot.commandservice.commands.profilecommand.ValCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RiotCommandManagerService extends ListenerAdapter {
    private final Map<String, RiotBotCommand> commands = new HashMap<>();
    private static final Set<String> LOL_BUTTONS = Set.of("lol_soloq", "lol_flex", "lol_history");
    private static final Set<String> VAL_BUTTONS = Set.of("val_profile", "val_history");

    private static final Button GITHUB_BUTTON = Button.link(
            "https://github.com/AgustinJak/riot-discord-bot", "GitHub")
            .withEmoji(Emoji.fromCustom("GitIcon", 1363525155573338212L, false));

    private static final Button DONATE_BUTTON = Button.link(
            "https://cafecito.app/aguspium", "Donaciones")
            .withEmoji(Emoji.fromUnicode("☕"));

    public RiotCommandManagerService() {
        addCommand("lol", new Profile());
        addCommand("tft", new TftCommand());
        addCommand("valorant", new ValCommand());
    }

    private void addCommand(String name, RiotBotCommand command) {
        commands.put(name, command);
    }

    private static final String TARGET_USER_ID = "816740476278931536";
    private static final String TARGET_IMAGE = "https://i.imgur.com/aCKJ7EA.png";
    private static final long COOLDOWN_MS = 10 * 60 * 1000;
    private long lastReplyTime = 0;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getAuthor().getId().equals(TARGET_USER_ID)) {
            long now = System.currentTimeMillis();
            if (now - lastReplyTime >= COOLDOWN_MS) {
                lastReplyTime = now;
                event.getMessage().reply(TARGET_IMAGE)
                    .addActionRow(DONATE_BUTTON)
                    .queue();
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        RiotBotCommand command = commands.get(event.getName());
        if (command == null) return;
        try {
            command.execute(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        String messageId = event.getMessageId();

        if (LOL_BUTTONS.contains(buttonId)) {
            ArrayList<byte[]> cards = Profile.cardCache.get(messageId);
            if (cards == null) return;

            if (buttonId.equals("lol_history")) {
                handleLolHistory(event, messageId);
            } else {
                handleProfileSwitch(event, messageId, buttonId, cards);
            }
        } else if (VAL_BUTTONS.contains(buttonId)) {
            if (buttonId.equals("val_history")) {
                handleValHistory(event, messageId);
            } else {
                handleValProfile(event, messageId);
            }
        }
    }

    private void handleProfileSwitch(ButtonInteractionEvent event, String messageId,
                                      String buttonId, ArrayList<byte[]> cards) {
        int index = buttonId.equals("lol_soloq") ? 0 : 1;
        byte[] card = cards.get(index);
        if (card == null) return;

        event.editMessageEmbeds().setAttachments(
            FileUpload.fromData(new ByteArrayInputStream(card), "rank_card.png")
        ).setActionRow(
            Button.primary("lol_soloq", "Solo Q").withDisabled(index == 0),
            Button.secondary("lol_flex", "Flex").withDisabled(index == 1),
            Button.secondary("lol_history", "Historial"),
            GITHUB_BUTTON,
            DONATE_BUTTON
        ).queue();
    }

    private void handleLolHistory(ButtonInteractionEvent event, String messageId) {
        MessageEmbed historyEmbed = Profile.historyCache.get(messageId);
        if (historyEmbed == null) return;

        event.deferEdit().queue();
        event.getHook().editOriginalEmbeds(historyEmbed).setAttachments().setActionRow(
            Button.secondary("lol_soloq", "Solo Q"),
            Button.secondary("lol_flex", "Flex"),
            Button.primary("lol_history", "Historial").asDisabled(),
            GITHUB_BUTTON,
            DONATE_BUTTON
        ).queue();
    }

    private void handleValHistory(ButtonInteractionEvent event, String messageId) {
        byte[] historyCard = ValCommand.historyCache.get(messageId);
        if (historyCard == null) return;

        event.deferEdit().queue();
        event.getHook().editOriginalAttachments(
            FileUpload.fromData(new ByteArrayInputStream(historyCard), "card.png")
        ).setActionRow(
            Button.secondary("val_profile", "Perfil"),
            Button.primary("val_history", "Historial").asDisabled(),
            GITHUB_BUTTON,
            DONATE_BUTTON
        ).queue();
    }

    private void handleValProfile(ButtonInteractionEvent event, String messageId) {
        byte[] card = ValCommand.cardCache.get(messageId);
        if (card == null) return;

        event.deferEdit().queue();
        event.getHook().editOriginalAttachments(
            FileUpload.fromData(new ByteArrayInputStream(card), "card.png")
        ).setActionRow(
            Button.primary("val_profile", "Perfil").asDisabled(),
            Button.secondary("val_history", "Historial"),
            GITHUB_BUTTON,
            DONATE_BUTTON
        ).queue();
    }
}
