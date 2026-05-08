package com.xphearts.data;

import com.xphearts.XPHearts;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final XPHearts plugin;
    private final File dataFile;
    private YamlConfiguration data;
    private final Map<UUID, Double> cache = new HashMap<>();

    public PlayerDataManager(XPHearts plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        load();
    }

    private void load() {
        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create data.yml: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : data.getKeys(false)) {
            try {
                // getDouble handles both legacy int values and new double values
                cache.put(UUID.fromString(key), data.getDouble(key + ".multiplier", 1.0));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save() {
        cache.forEach((uuid, mult) -> data.set(uuid + ".multiplier", mult));
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save data.yml: " + e.getMessage());
        }
    }

    public double getMultiplier(UUID uuid) {
        return cache.getOrDefault(uuid, 1.0);
    }

    public void setMultiplier(UUID uuid, double value) {
        double max = plugin.getConfig().getDouble("multiplier.max-multiplier", 10.0);
        cache.put(uuid, Math.max(1.0, Math.min(value, max)));
        save();
    }

    public void resetMultiplier(UUID uuid) {
        cache.put(uuid, 1.0);
        save();
    }
}
