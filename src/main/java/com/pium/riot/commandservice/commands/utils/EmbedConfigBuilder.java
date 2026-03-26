package com.pium.riot.commandservice.commands.utils;

import lombok.Builder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

@Builder
public class EmbedConfigBuilder {
    private String authorName;
    private String authorLink;
    private String authorIcon;
    private String title;
    private String description;
    private String field;
    private String inField;
    private String secondField;
    private String secondInField;
    private String footer;
    private Color color;
    private String thumbnailUrlImage;
    private String urlImage;

    public MessageEmbed buildEmbed() {
        EmbedBuilder eb = new EmbedBuilder();

        if (authorName != null) eb.setAuthor(authorName, authorLink, authorIcon);
        if (title != null) eb.setTitle(title);
        if (description != null) eb.setDescription(description);
        if (field != null && inField != null) eb.addField(field, inField, false);
        if (secondField != null && secondInField != null) eb.addField(secondField, secondInField, false);
        if (footer != null) eb.setFooter(footer);
        if (color != null) eb.setColor(color);
        if (thumbnailUrlImage != null) eb.setThumbnail(thumbnailUrlImage);
        if (urlImage != null) eb.setImage(urlImage);

        return eb.build();
    }
}