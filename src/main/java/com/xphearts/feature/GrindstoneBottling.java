package com.xphearts.feature;

import com.xphearts.XPHearts;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;

public class GrindstoneBottling implements Listener {

    private final XPHearts plugin;

    public GrindstoneBottling(XPHearts plugin) {
        this.plugin = plugin;
    }

    // Force the grindstone to display a result when slot 0 has the enchanted item
    // and slot 1 has a glass bottle. Without this, vanilla shows no result because
    // a glass bottle is not valid grindstone equipment.
    @EventHandler
    public void onGrindstonePrepare(PrepareGrindstoneEvent event) {
        if (!plugin.getConfig().getBoolean("grindstone-bottling.enabled", true)) return;

        GrindstoneInventory grindstone = event.getInventory();
        ItemStack slot0 = grindstone.getItem(0);
        ItemStack slot1 = grindstone.getItem(1);

        if (!isGlassBottle(slot1)) return;

        if (plugin.getConfig().getBoolean("grindstone-bottling.require-dispatch-xp", true)) {
            if (!hasEnchantments(slot0)) return;
        } else {
            if (slot0 == null || slot0.getType() == Material.AIR) return;
        }

        ItemStack result = stripEnchantments(slot0.clone());
        event.setResult(result);
    }

    // When shift-clicking a glass bottle from the player's inventory into an open
    // grindstone, route it directly to slot 1 (second/bottom input slot).
    @EventHandler
    public void onShiftClickBottle(InventoryClickEvent event) {
        if (!plugin.getConfig().getBoolean("grindstone-bottling.enabled", true)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getView().getTopInventory() instanceof GrindstoneInventory grindstone)) return;
        if (!event.isShiftClick()) return;

        // Only intercept clicks originating from the player's own inventory
        if (event.getClickedInventory() == null || event.getClickedInventory() == grindstone) return;

        ItemStack clicked = event.getCurrentItem();
        if (!isGlassBottle(clicked)) return;

        // Slot 1 must be empty; never displace an existing item
        ItemStack slot1 = grindstone.getItem(1);
        if (slot1 != null && slot1.getType() != Material.AIR) return;

        event.setCancelled(true);

        grindstone.setItem(1, new ItemStack(Material.GLASS_BOTTLE, 1));

        if (clicked.getAmount() > 1) {
            clicked.setAmount(clicked.getAmount() - 1);
        } else {
            event.setCurrentItem(null);
        }

        player.updateInventory();
    }

    // When the player clicks the grindstone result slot, check that a glass bottle
    // is in slot 1. If so, consume it and give the disenchanted item + Bottle o'
    // Enchanting. Inventory fallback is intentionally removed — bottle must be in
    // the grindstone itself.
    @EventHandler
    public void onGrindstoneClick(InventoryClickEvent event) {
        if (!plugin.getConfig().getBoolean("grindstone-bottling.enabled", true)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getView().getTopInventory() instanceof GrindstoneInventory grindstone)) return;
        if (event.getRawSlot() != 2) return;

        ItemStack result = grindstone.getResult();
        if (result == null || result.getType() == Material.AIR) return;

        // Glass bottle must be physically in slot 1 of the grindstone
        if (!isGlassBottle(grindstone.getItem(1))) return;

        if (plugin.getConfig().getBoolean("grindstone-bottling.require-dispatch-xp", true)) {
            if (!hasEnchantments(grindstone.getItem(0))) return;
        }

        event.setCancelled(true);

        decrementGrindstoneSlot(grindstone, 1);
        grindstone.setItem(0, null);
        grindstone.setResult(null);

        addOrDrop(player, result.clone());
        int count = plugin.getConfig().getInt("grindstone-bottling.bottles-per-dispatch", 1);
        addOrDrop(player, new ItemStack(Material.EXPERIENCE_BOTTLE, count));

        player.updateInventory();
    }

    // Returns the disenchanted item. For enchanted books this is a plain BOOK
    // (a new ItemStack) rather than an empty ENCHANTED_BOOK.
    private ItemStack stripEnchantments(ItemStack item) {
        new ArrayList<>(item.getEnchantments().keySet()).forEach(item::removeEnchantment);
        if (item.getItemMeta() instanceof EnchantmentStorageMeta bookMeta) {
            new ArrayList<>(bookMeta.getStoredEnchants().keySet()).forEach(bookMeta::removeStoredEnchant);
            item.setItemMeta(bookMeta);
            return new ItemStack(Material.BOOK, item.getAmount());
        }
        return item;
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
