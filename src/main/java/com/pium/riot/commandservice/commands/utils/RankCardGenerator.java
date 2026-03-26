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

public class RankCardGenerator {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 200;
    private static final int PADDING = 20;
    private static final int ICON_SIZE = 100;
    private static final Color BG_COLOR = new Color(24, 25, 28);
    private static final Color BG_INNER = new Color(32, 34, 37);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(185, 185, 185);
    private static final Color BAR_BG = new Color(55, 57, 63);
    private static final Color WIN_COLOR = new Color(40, 167, 69);
    private static final Color LOSS_COLOR = new Color(220, 53, 69);

    public static InputStream generate(String riotUser, String tier, String rank,
                                        int lp, int wins, int losses,
                                        String queueName, Color tierColor,
                                        String rankIconUrl) throws IOException {
        return generate(riotUser, tier, rank, lp, wins, losses, queueName, tierColor, rankIconUrl, null);
    }

    public static InputStream generate(String riotUser, String tier, String rank,
                                        int lp, int wins, int losses,
                                        String queueName, Color tierColor,
                                        String rankIconUrl, String splashUrl) throws IOException {

        BufferedImage card = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = card.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        drawBackground(g, splashUrl, tierColor);
        drawRankIcon(g, rankIconUrl);
        drawPlayerInfo(g, riotUser, tier, rank, lp, queueName, tierColor);
        drawWinrateBar(g, wins, losses);

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(card, "png", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private static void drawBackground(Graphics2D g, String splashUrl, Color tierColor) {
        g.setColor(BG_COLOR);
        g.fill(new RoundRectangle2D.Float(0, 0, WIDTH, HEIGHT, 16, 16));

        g.setColor(BG_INNER);
        g.fill(new RoundRectangle2D.Float(8, 8, WIDTH - 16, HEIGHT - 16, 12, 12));

        if (splashUrl == null) return;

        try {
            BufferedImage splash = ImageIO.read(URI.create(splashUrl).toURL());
            if (splash == null) return;

            Shape innerClip = new RoundRectangle2D.Float(8, 8, WIDTH - 16, HEIGHT - 16, 12, 12);
            Shape oldClip = g.getClip();
            g.setClip(innerClip);

            int srcW = splash.getWidth();
            int srcH = splash.getHeight();
            double scale = Math.max((double) (WIDTH - 16) / srcW, (double) (HEIGHT - 16) / srcH);
            int drawW = (int) (srcW * scale);
            int drawH = (int) (srcH * scale);
            int drawX = 8 + (WIDTH - 16 - drawW) / 2;
            int drawY = 8 + (HEIGHT - 16 - drawH) / 2;

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
            g.drawImage(splash, drawX, drawY, drawW, drawH, null);
            g.setComposite(AlphaComposite.SrcOver);

            Color gradStart = new Color(BG_INNER.getRed(), BG_INNER.getGreen(), BG_INNER.getBlue(), 230);
            Color gradEnd = new Color(BG_INNER.getRed(), BG_INNER.getGreen(), BG_INNER.getBlue(), 60);
            GradientPaint fade = new GradientPaint(8, 0, gradStart, WIDTH * 0.55f, 0, gradEnd);
            g.setPaint(fade);
            g.fill(innerClip);

            Color tint = tierColor != null ? tierColor : new Color(100, 100, 100);
            Color bottomStart = new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), 0);
            Color bottomEnd = new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), 50);
            GradientPaint bottomGlow = new GradientPaint(0, HEIGHT * 0.5f, bottomStart, 0, HEIGHT - 8, bottomEnd);
            g.setPaint(bottomGlow);
            g.fill(innerClip);

            g.setClip(oldClip);
        } catch (Exception ignored) {
        }
    }

    private static void drawRankIcon(Graphics2D g, String rankIconUrl) {
        try {
            BufferedImage icon = ImageIO.read(URI.create(rankIconUrl).toURL());
            int x = PADDING + 10;
            int y = (HEIGHT - ICON_SIZE) / 2;
            g.drawImage(icon, x, y, ICON_SIZE, ICON_SIZE, null);
        } catch (Exception ignored) {
        }
    }

    private static void drawPlayerInfo(Graphics2D g, String riotUser, String tier, String rank,
                                        int lp, String queueName, Color tierColor) {
        int textX = PADDING + ICON_SIZE + 30;

        g.setColor(TEXT_SECONDARY);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g.drawString(queueName, textX, 40);

        g.setColor(tierColor != null ? tierColor : TEXT_PRIMARY);
        g.setFont(new Font("Segoe UI", Font.BOLD, 26));
        g.drawString(tier + " " + rank, textX, 72);

        g.setColor(TEXT_PRIMARY);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.drawString(lp + " LP", textX, 96);

        g.setColor(TEXT_SECONDARY);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g.drawString(riotUser, textX, 120);
    }

    private static void drawWinrateBar(Graphics2D g, int wins, int losses) {
        int total = wins + losses;
        int barX = PADDING + ICON_SIZE + 30;
        int barY = 140;
        int barWidth = WIDTH - barX - PADDING - 10;
        int barHeight = 18;

        g.setColor(BAR_BG);
        g.fill(new RoundRectangle2D.Float(barX, barY, barWidth, barHeight, 10, 10));

        if (total > 0) {
            int winWidth = (int) ((double) wins / total * barWidth);
            if (winWidth > 0) {
                g.setColor(WIN_COLOR);
                g.fill(new RoundRectangle2D.Float(barX, barY, winWidth, barHeight, 10, 10));
                if (winWidth < barWidth) {
                    g.fillRect(barX + winWidth - 5, barY, 5, barHeight);
                }
            }
            if (winWidth < barWidth) {
                g.setColor(LOSS_COLOR);
                g.fill(new RoundRectangle2D.Float(barX + winWidth, barY, barWidth - winWidth, barHeight, 10, 10));
                if (winWidth > 0) {
                    g.fillRect(barX + winWidth, barY, 5, barHeight);
                }
            }
        }

        g.setColor(TEXT_PRIMARY);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        int percentage = total > 0 ? (int) Math.round((double) wins / total * 100) : 0;
        String stats = wins + "W " + losses + "L — " + percentage + "%";
        FontMetrics fm = g.getFontMetrics();
        int statsX = barX + (barWidth - fm.stringWidth(stats)) / 2;
        g.drawString(stats, statsX, barY + barHeight + 16);
    }
}
