package com.pium.riot.commandservice.commands.utils;

public class ProgressBar {

    private static final String FILLED = "\u2588";
    private static final String EMPTY = "\u2591";
    private static final int BAR_LENGTH = 10;

    public static String build(int value, int max) {
        if (max == 0) return EMPTY.repeat(BAR_LENGTH) + " 0%";
        int percentage = (int) Math.round(((double) value / max) * 100);
        int filled = (int) Math.round(((double) value / max) * BAR_LENGTH);
        int empty = BAR_LENGTH - filled;
        return FILLED.repeat(filled) + EMPTY.repeat(empty) + " " + percentage + "%";
    }
}
