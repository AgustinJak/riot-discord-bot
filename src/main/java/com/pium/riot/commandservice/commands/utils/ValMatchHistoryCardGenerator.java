package com.pium.riot.commandservice.commands.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public class ValMatchHistoryCardGenerator {

    private static final int WIDTH = 750;
    private static final int HEADER_HEIGHT = 90;
    private static final int ROW_HEIGHT = 90;
    private static final int ROW_GAP = 4;
    private static final int PADDING = 16;

    private static final Color BG_COLOR = new Color(15, 17, 21);
    private static final Color WIN_ACCENT = new Color(74, 222, 128);
    private static final Color LOSS_ACCENT = new Color(239, 68, 68);
    private static final Color WIN_BG = new Color(30, 45, 35);
    private static final Color LOSS_BG = new Color(50, 28, 30);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(160, 165, 175);
    private static final Color TEXT_MUTED = new Color(100, 105, 115);
    private static final Color BAR_BG = new Color(50, 53, 60);
    private static final Color RR_BAR_COLOR = new Color(74, 222, 128);

    public static InputStream generate(String rankName, int rr, String rankIconUrl,
                                        Color tierColor, List<MatchRow> matches) throws IOException {
        int matchCount = Math.min(matches.size(), 5);
        int totalHeight = HEADER_HEIGHT + PADDING + matchCount * (ROW_HEIGHT + ROW_GAP) + PADDING;

        BufferedImage card = new BufferedImage(WIDTH, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = card.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.setColor(BG_COLOR);
        g.fill(new RoundRectangle2D.Float(0, 0, WIDTH, totalHeight, 16, 16));

        drawHeader(g, rankName, rr, rankIconUrl, tierColor);

        int y = HEADER_HEIGHT + PADDING;
        for (int i = 0; i < matchCount; i++) {
            drawMatchRow(g, matches.get(i), y);
            y += ROW_HEIGHT + ROW_GAP;
        }

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(card, "png", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private static void drawHeader(Graphics2D g, String rankName, int rr,
                                    String rankIconUrl, Color tierColor) {
        int iconSize = 50;
        int iconX = WIDTH / 2 - iconSize / 2;
        int iconY = 8;

        try {
            BufferedImage icon = ImageIO.read(URI.create(rankIconUrl).toURL());
            if (icon != null) {
                g.drawImage(icon, iconX, iconY, iconSize, iconSize, null);
            }
        } catch (Exception ignored) {}

        g.setColor(tierColor != null ? tierColor : TEXT_PRIMARY);
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        int textW = fm.stringWidth(rankName);
        g.drawString(rankName, (WIDTH - textW) / 2, iconY + iconSize + 18);

        int barWidth = 200;
        int barHeight = 6;
        int barX = WIDTH / 2 - barWidth / 2;
        int barY = iconY + iconSize + 26;

        g.setColor(BAR_BG);
        g.fill(new RoundRectangle2D.Float(barX, barY, barWidth, barHeight, 4, 4));

        int fillWidth = (int) ((double) rr / 100 * barWidth);
        if (fillWidth > 0) {
            g.setColor(RR_BAR_COLOR);
            g.fill(new RoundRectangle2D.Float(barX, barY, fillWidth, barHeight, 4, 4));
        }

        g.setColor(TEXT_MUTED);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        fm = g.getFontMetrics();
        String rrText = rr + "/100";
        g.drawString(rrText, barX + barWidth + 8, barY + barHeight - 1);

        String label = "CLASIFICACIÓN DE RANGO";
        int labelW = fm.stringWidth(label);
        g.drawString(label, barX - labelW - 8, barY + barHeight - 1);
    }

    private static void drawMatchRow(Graphics2D g, MatchRow match, int y) {
        Color rowBg = match.win ? WIN_BG : LOSS_BG;
        Color accent = match.win ? WIN_ACCENT : LOSS_ACCENT;

        int rowX = PADDING;
        int rowW = WIDTH - 2 * PADDING;
        Shape rowClip = new RoundRectangle2D.Float(rowX, y, rowW, ROW_HEIGHT, 10, 10);

        g.setColor(rowBg);
        g.fill(rowClip);

        drawMapBackground(g, match.mapImageUrl, rowClip, rowX, y, rowW, rowBg);

        g.setColor(accent);
        g.fill(new RoundRectangle2D.Float(rowX, y, 4, ROW_HEIGHT, 4, 4));

        drawAgentIcon(g, match.agentIconUrl, rowX + 12, y + (ROW_HEIGHT - 65) / 2, 65);

        int statsX = rowX + 12 + 65 + 14;

        g.setColor(TEXT_SECONDARY);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString("KDA", statsX, y + 30);

        g.setColor(TEXT_PRIMARY);
        g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        String kda = match.kills + " / " + match.deaths + " / " + match.assists;
        g.drawString(kda, statsX + 35, y + 30);

        g.setColor(TEXT_SECONDARY);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString("PUNTUACIÓN", statsX, y + 54);

        g.setColor(TEXT_PRIMARY);
        g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g.drawString(String.valueOf(match.score), statsX + 90, y + 54);

        g.setColor(TEXT_MUTED);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g.drawString(match.mapName, statsX, y + 74);

        int resultX = rowX + rowW / 2 + 30;

        g.setColor(accent);
        g.setFont(new Font("Segoe UI", Font.BOLD, 24));
        String resultText = match.win ? "VICTORIA" : "DERROTA";
        g.drawString(resultText, resultX, y + 40);

        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        String wonStr = String.valueOf(match.roundsWon);
        String dash = "-";
        String lostStr = String.valueOf(match.roundsLost);
        FontMetrics fm = g.getFontMetrics();

        int roundsX = resultX + 40;
        g.setColor(TEXT_PRIMARY);
        g.drawString(wonStr, roundsX, y + 64);
        int x2 = roundsX + fm.stringWidth(wonStr);
        g.setColor(TEXT_MUTED);
        g.drawString(dash, x2, y + 64);
        int x3 = x2 + fm.stringWidth(dash);
        g.setColor(accent);
        g.drawString(lostStr, x3, y + 64);

        if (match.win && match.mvp) {
            int mvpX = resultX + 180;
            int mvpY = y + 22;
            g.setColor(new Color(255, 215, 0, 35));
            g.fill(new RoundRectangle2D.Float(mvpX, mvpY, 76, 38, 8, 8));
            g.setColor(new Color(255, 215, 0));
            g.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g.drawString("MVP DE", mvpX + 15, mvpY + 16);
            g.drawString("PARTIDA", mvpX + 11, mvpY + 30);
        }
    }

    private static void drawAgentIcon(Graphics2D g, String iconUrl, int x, int y, int size) {
        if (iconUrl == null || iconUrl.isEmpty()) return;
        try {
            BufferedImage icon = ImageIO.read(URI.create(iconUrl).toURL());
            if (icon == null) return;
            Shape oldClip = g.getClip();
            g.setClip(new RoundRectangle2D.Float(x, y, size, size, 10, 10));
            g.drawImage(icon, x, y, size, size, null);
            g.setClip(oldClip);
        } catch (Exception ignored) {}
    }

    private static void drawMapBackground(Graphics2D g, String mapImageUrl,
                                            Shape rowClip, int rowX, int y,
                                            int rowW, Color rowBg) {
        if (mapImageUrl == null || mapImageUrl.isEmpty()) return;
        try {
            BufferedImage mapImg = ImageIO.read(URI.create(mapImageUrl).toURL());
            if (mapImg == null) return;

            Shape oldClip = g.getClip();
            g.setClip(rowClip);

            int mapDrawW = (int) (rowW * 0.45);
            int mapDrawX = rowX + rowW - mapDrawW;

            int srcW = mapImg.getWidth();
            int srcH = mapImg.getHeight();
            double scale = Math.max((double) mapDrawW / srcW, (double) ROW_HEIGHT / srcH);
            int scaledW = (int) (srcW * scale);
            int scaledH = (int) (srcH * scale);
            int drawX = mapDrawX + (mapDrawW - scaledW) / 2;
            int drawY = y + (ROW_HEIGHT - scaledH) / 2;

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g.drawImage(mapImg, drawX, drawY, scaledW, scaledH, null);
            g.setComposite(AlphaComposite.SrcOver);

            Color fadeStart = new Color(rowBg.getRed(), rowBg.getGreen(), rowBg.getBlue(), 255);
            Color fadeEnd = new Color(rowBg.getRed(), rowBg.getGreen(), rowBg.getBlue(), 0);
            GradientPaint fade = new GradientPaint(mapDrawX, 0, fadeStart, mapDrawX + mapDrawW * 0.35f, 0, fadeEnd);
            g.setPaint(fade);
            g.fillRect(mapDrawX, y, mapDrawW, ROW_HEIGHT);

            g.setClip(oldClip);
        } catch (Exception ignored) {}
    }

    public static class MatchRow {
        public String agentIconUrl;
        public String agentName;
        public int kills;
        public int deaths;
        public int assists;
        public int score;
        public String mapName;
        public String mapImageUrl;
        public boolean win;
        public int roundsWon;
        public int roundsLost;
        public boolean mvp;

        public MatchRow(String agentIconUrl, String agentName,
                        int kills, int deaths, int assists, int score,
                        String mapName, String mapImageUrl, boolean win,
                        int roundsWon, int roundsLost, boolean mvp) {
            this.agentIconUrl = agentIconUrl;
            this.agentName = agentName;
            this.kills = kills;
            this.deaths = deaths;
            this.assists = assists;
            this.score = score;
            this.mapName = mapName;
            this.mapImageUrl = mapImageUrl;
            this.win = win;
            this.roundsWon = roundsWon;
            this.roundsLost = roundsLost;
            this.mvp = mvp;
        }
    }
}
