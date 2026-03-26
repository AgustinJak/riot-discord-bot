package com.pium.riot.botservice.config;

import com.pium.riot.botservice.config.utils.ValRegion;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ValRegionConfig {
    public static OptionData getRegionOptions() {
        OptionData regionOption = new OptionData(OptionType.STRING, "region", "Selecciona tu región", true);
        ValRegion.getRegionMap().forEach(regionOption::addChoice);
        return regionOption;
    }
}
