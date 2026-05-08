package com.xphearts.feature;

import com.xphearts.XPHearts;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmokingRecipe;

public class RottenFleshSmelting implements Listener {

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

    // Rotten flesh is not natively recognised as a furnace ingredient by the
    // vanilla shift-click logic, so we intercept it and move it to the input
    // slot (slot 0) ourselves. Fuel slot and output slot are never touched.
    @EventHandler
    public void onShiftClickRottenFlesh(InventoryClickEvent event) {
        if (!plugin.getConfig().getBoolean("rotten-flesh-smelting.enabled", true)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.isShiftClick()) return;

        Inventory topInv = event.getView().getTopInventory();
        if (!(topInv instanceof FurnaceInventory furnace)) return;

        // Only handle clicks from the player's own inventory, not from inside the furnace
        if (event.getClickedInventory() == null || event.getClickedInventory() == topInv) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.ROTTEN_FLESH) return;

        ItemStack currentInput = furnace.getItem(0);

        if (currentInput == null || currentInput.getType() == Material.AIR) {
            event.setCancelled(true);
            furnace.setItem(0, clicked.clone());
            event.setCurrentItem(null);
        } else if (currentInput.getType() == Material.ROTTEN_FLESH) {
            int available = currentInput.getMaxStackSize() - currentInput.getAmount();
            if (available <= 0) return;
            event.setCancelled(true);
            int toMove = Math.min(available, clicked.getAmount());
            ItemStack merged = currentInput.clone();
            merged.setAmount(currentInput.getAmount() + toMove);
            furnace.setItem(0, merged);
            if (clicked.getAmount() > toMove) {
                clicked.setAmount(clicked.getAmount() - toMove);
            } else {
                event.setCurrentItem(null);
            }
        }
        // If slot 0 holds a different item, leave it alone

        player.updateInventory();
    }
}
