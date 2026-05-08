package com.xphearts;

import com.xphearts.command.XPHeartsCommand;
import com.xphearts.command.XPMultiplierCommand;
import com.xphearts.data.PlayerDataManager;
import com.xphearts.feature.CharmListener;
import com.xphearts.feature.CharmManager;
import com.xphearts.feature.GrindstoneBottling;
import com.xphearts.feature.RottenFleshSmelting;
import com.xphearts.integration.XPHeartsExpansion;
import com.xphearts.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class XPHearts extends JavaPlugin {

    public static final String VERSION = "1.3.1";

    private static XPHearts instance;
    private PlayerDataManager dataManager;
    private CharmManager charmManager;
    private RottenFleshSmelting fleshSmelting;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        dataManager   = new PlayerDataManager(this);
        charmManager  = new CharmManager(this);
        fleshSmelting = new RottenFleshSmelting(this);
        fleshSmelting.register();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GrindstoneBottling(this), this);
        Bukkit.getPluginManager().registerEvents(new CharmListener(this, charmManager, dataManager), this);
        Bukkit.getPluginManager().registerEvents(fleshSmelting, this);

        getCommand("xphearts").setExecutor(new XPHeartsCommand(this));
        getCommand("xpmultiplier").setExecutor(new XPMultiplierCommand(dataManager));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new XPHeartsExpansion(this).register();
            getLogger().info("PlaceholderAPI found - placeholders registered.");
        }

        getLogger().info("XPHearts v" + VERSION + " enabled.");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) dataManager.save();
        getLogger().info("XPHearts disabled.");
    }

    public static XPHearts getInstance() { return instance; }
    public PlayerDataManager getDataManager() { return dataManager; }
    public CharmManager getCharmManager() { return charmManager; }

    // Called by /xphearts reload — handles config, smelting, and player health in one shot
    public void reloadFeatures() {
        reloadConfig();

        fleshSmelting.unregister();
        if (getConfig().getBoolean("rotten-flesh-smelting.enabled", true)) {
            fleshSmelting.register();
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            updateHealth(p);
        }
    }

    public double calculateMaxHealth(int level) {
        // Guard against bad config values to prevent division-by-zero or negative cap
        int levelsPerHalfHeart = Math.max(1, getConfig().getInt("levels-per-half-heart", 5));
        int baseHearts         = Math.max(1, getConfig().getInt("base-hearts", 10));
        int maxHearts          = Math.max(baseHearts, getConfig().getInt("max-hearts", 20));
        boolean halfHeartsOn   = getConfig().getBoolean("half-hearts-enabled", true);

        int extraHalfHearts = level / levelsPerHalfHeart;
        int cap             = (maxHearts - baseHearts) * 2;
        extraHalfHearts     = Math.min(extraHalfHearts, cap);

        // When half-hearts are disabled, round down to the nearest full heart
        if (!halfHeartsOn) {
            extraHalfHearts = (extraHalfHearts / 2) * 2;
        }

        return (baseHearts * 2.0) + extraHalfHearts;
    }

    public void updateHealth(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        if (attr == null) return;
        double maxHp = calculateMaxHealth(player.getLevel());
        attr.setBaseValue(maxHp);
        if (player.getHealth() > maxHp) {
            player.setHealth(maxHp);
        }
    }
}
