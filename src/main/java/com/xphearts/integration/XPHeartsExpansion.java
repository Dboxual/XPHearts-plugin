package com.xphearts.integration;

import com.xphearts.XPHearts;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class XPHeartsExpansion extends PlaceholderExpansion {

    private final XPHearts plugin;

    public XPHeartsExpansion(XPHearts plugin) {
        this.plugin = plugin;
    }

    @Override public @NotNull String getIdentifier() { return "xphearts"; }
    @Override public @NotNull String getAuthor()     { return "XPHearts"; }
    @Override public @NotNull String getVersion()    { return XPHearts.VERSION; }
    @Override public boolean persist()               { return true; }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) return "";

        double multiplier = plugin.getDataManager().getMultiplier(offlinePlayer.getUniqueId());

        return switch (params.toLowerCase()) {
            case "multiplier"     -> fmt(multiplier) + "x";
            case "multiplier_raw" -> fmt(multiplier);
            case "hearts" -> {
                if (!(offlinePlayer instanceof Player p)) yield "?";
                yield formatHearts(plugin.calculateMaxHealth(p.getLevel()) / 2.0);
            }
            case "extra_hearts" -> {
                if (!(offlinePlayer instanceof Player p)) yield "?";
                double extra = (plugin.calculateMaxHealth(p.getLevel()) / 2.0)
                        - plugin.getConfig().getInt("base-hearts", 10);
                yield (extra > 0 ? "+" : "") + formatHearts(extra);
            }
            default -> null;
        };
    }

    // Renders whole numbers without a trailing ".0" (e.g. 2.0 → "2", 1.5 → "1.5")
    private static String fmt(double v) {
        return v % 1 == 0 ? String.valueOf((int) v) : String.format("%.1f", v);
    }

    private static String formatHearts(double val) {
        return (val == Math.floor(val)) ? String.valueOf((int) val) : String.valueOf(val);
    }
}
