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
    private final Map<UUID, Integer> cache = new HashMap<>();

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
                cache.put(UUID.fromString(key), data.getInt(key + ".multiplier", 1));
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

    public int getMultiplier(UUID uuid) {
        return cache.getOrDefault(uuid, 1);
    }

    public void setMultiplier(UUID uuid, int value) {
        int max = plugin.getConfig().getInt("multiplier.max-multiplier", 10);
        cache.put(uuid, Math.max(1, Math.min(value, max)));
        save();
    }

    public void resetMultiplier(UUID uuid) {
        cache.put(uuid, 1);
        save();
    }
}
