package com.pium.riot.botservice;

import com.pium.riot.botservice.config.RegionConfig;
import com.pium.riot.botservice.config.ValRegionConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandRegisterService {
    private final JDA api;

    public CommandRegisterService(JDA api) {
        this.api = api;
    }

    public void registerCommands() {
        api.getGuilds().forEach(guild -> guild.updateCommands().addCommands(
                Commands.slash("profile", "Muestra tu perfil de League of Legends")
                        .addOption(OptionType.STRING, "nick", "Ingresa tu RiotUser", true)
                        .addOption(OptionType.STRING, "tag", "Ingresa tu tag sin el #", true)
                        .addOptions(RegionConfig.getRegionOptions()),
                Commands.slash("tft", "Muestra tu perfil de Teamfight Tactics")
                        .addOption(OptionType.STRING, "nick", "Ingresa tu RiotUser", true)
                        .addOption(OptionType.STRING, "tag", "Ingresa tu tag sin el #", true)
                        .addOptions(RegionConfig.getRegionOptions()),
                Commands.slash("val", "Muestra tu perfil de Valorant")
                        .addOption(OptionType.STRING, "nick", "Ingresa tu RiotUser", true)
                        .addOption(OptionType.STRING, "tag", "Ingresa tu tag sin el #", true)
                        .addOptions(ValRegionConfig.getRegionOptions())
        ).complete());
    }
}
