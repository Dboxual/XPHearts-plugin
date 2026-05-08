package com.xphearts.feature;

import com.xphearts.XPHearts;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmokingRecipe;

public class RottenFleshSmelting {

    private final XPHearts plugin;
    private final NamespacedKey furnaceKey;
    private final NamespacedKey blastKey;
    private final NamespacedKey smokerKey;

    public RottenFleshSmelting(XPHearts plugin) {
        this.plugin = plugin;
        this.furnaceKey = new NamespacedKey(plugin, "rotten_flesh_furnace");
        this.blastKey   = new NamespacedKey(plugin, "rotten_flesh_blast");
        this.smokerKey  = new NamespacedKey(plugin, "rotten_flesh_smoker");
    }

    public void register() {
        if (!plugin.getConfig().getBoolean("rotten-flesh-smelting.enabled", true)) return;

        int cookTime = plugin.getConfig().getInt("rotten-flesh-smelting.cooking-time", 200);
        float xp     = (float) plugin.getConfig().getDouble("rotten-flesh-smelting.experience", 0.1);
        ItemStack leather = new ItemStack(Material.LEATHER);

        // Standard furnace
        plugin.getServer().addRecipe(
                new FurnaceRecipe(furnaceKey, leather, Material.ROTTEN_FLESH, xp, cookTime));

        // Blast furnace (half cooking time)
        plugin.getServer().addRecipe(
                new BlastingRecipe(blastKey, leather, Material.ROTTEN_FLESH, xp, cookTime / 2));

        // Smoker (rotten flesh is food, so smoker accepts it)
        plugin.getServer().addRecipe(
                new SmokingRecipe(smokerKey, leather, Material.ROTTEN_FLESH, xp, cookTime / 2));

        plugin.getLogger().info("Rotten flesh smelting recipes registered (furnace, blast, smoker).");
    }

    public void unregister() {
        plugin.getServer().removeRecipe(furnaceKey);
        plugin.getServer().removeRecipe(blastKey);
        plugin.getServer().removeRecipe(smokerKey);
    }
}
