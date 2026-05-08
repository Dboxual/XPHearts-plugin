package com.xphearts.feature;

import com.xphearts.XPHearts;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class GrindstoneBottling implements Listener {

    private static final int OFFHAND_SLOT = -2;
    private static final int NO_BOTTLE    = Integer.MIN_VALUE;

    private final XPHearts plugin;

    public GrindstoneBottling(XPHearts plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGrindstoneClick(InventoryClickEvent event) {
        if (!plugin.getConfig().getBoolean("grindstone-bottling.enabled", true)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getView().getTopInventory() instanceof GrindstoneInventory grindstone)) return;
        if (event.getRawSlot() != 2) return; // result slot only

        ItemStack result = grindstone.getResult();
        if (result == null || result.getType() == Material.AIR) return;

        ItemStack slot0 = grindstone.getItem(0);
        ItemStack slot1 = grindstone.getItem(1);

        if (plugin.getConfig().getBoolean("grindstone-bottling.require-dispatch-xp", true)) {
            if (!hasEnchantments(slot0) && !hasEnchantments(slot1)) return;
        }

        // Check grindstone slots first (works if server allows bottles there),
        // then fall back to the player's offhand and inventory
        boolean bottleInGrindstone = false;
        int grindstoneBottleSlot   = -1;
        int playerBottleSlot       = NO_BOTTLE;

        if (isGlassBottle(slot0) && !isGlassBottle(slot1)) {
            bottleInGrindstone = true;
            grindstoneBottleSlot = 0;
        } else if (isGlassBottle(slot1) && !isGlassBottle(slot0)) {
            bottleInGrindstone = true;
            grindstoneBottleSlot = 1;
        } else {
            playerBottleSlot = findPlayerBottleSlot(player);
            if (playerBottleSlot == NO_BOTTLE) return;
        }

        event.setCancelled(true);

        if (bottleInGrindstone) {
            decrementGrindstoneSlot(grindstone, grindstoneBottleSlot);
            grindstone.setItem(grindstoneBottleSlot == 0 ? 1 : 0, null);
        } else {
            decrementPlayerSlot(player, playerBottleSlot);
            grindstone.setItem(0, null);
            grindstone.setItem(1, null);
        }
        grindstone.setResult(null);

        addOrDrop(player, result.clone());
        int count = plugin.getConfig().getInt("grindstone-bottling.bottles-per-dispatch", 1);
        addOrDrop(player, new ItemStack(Material.EXPERIENCE_BOTTLE, count));

        player.updateInventory();
    }

    private int findPlayerBottleSlot(Player player) {
        if (isGlassBottle(player.getInventory().getItemInOffHand())) return OFFHAND_SLOT;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (isGlassBottle(contents[i])) return i;
        }
        return NO_BOTTLE;
    }

    private void decrementGrindstoneSlot(GrindstoneInventory inv, int slot) {
        ItemStack item = inv.getItem(slot);
        if (item == null) return;
        if (item.getAmount() > 1) {
            ItemStack copy = item.clone();
            copy.setAmount(item.getAmount() - 1);
            inv.setItem(slot, copy);
        } else {
            inv.setItem(slot, null);
        }
    }

    private void decrementPlayerSlot(Player player, int slot) {
        PlayerInventory inv = player.getInventory();
        ItemStack item = (slot == OFFHAND_SLOT) ? inv.getItemInOffHand() : inv.getItem(slot);
        if (item == null) return;
        if (item.getAmount() > 1) {
            ItemStack copy = item.clone();
            copy.setAmount(item.getAmount() - 1);
            if (slot == OFFHAND_SLOT) inv.setItemInOffHand(copy);
            else inv.setItem(slot, copy);
        } else {
            if (slot == OFFHAND_SLOT) inv.setItemInOffHand(null);
            else inv.setItem(slot, null);
        }
    }

    private boolean isGlassBottle(ItemStack item) {
        return item != null && item.getType() == Material.GLASS_BOTTLE;
    }

    private boolean hasEnchantments(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.getEnchantments().isEmpty()) return true;
        if (item.getItemMeta() instanceof EnchantmentStorageMeta meta) {
            return !meta.getStoredEnchants().isEmpty();
        }
        return false;
    }

    private void addOrDrop(Player player, ItemStack item) {
        player.getInventory().addItem(item).values()
                .forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }
}
