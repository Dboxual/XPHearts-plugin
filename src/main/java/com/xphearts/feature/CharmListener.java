package com.xphearts.feature;

import com.xphearts.XPHearts;
import com.xphearts.data.PlayerDataManager;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CharmListener implements Listener {

    private final XPHearts plugin;
    private final CharmManager charmManager;
    private final PlayerDataManager dataManager;

    public CharmListener(XPHearts plugin, CharmManager charmManager, PlayerDataManager dataManager) {
        this.plugin = plugin;
        this.charmManager = charmManager;
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        if (!plugin.getConfig().getBoolean("multiplier.enabled", true)) return;
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        double multiplier = dataManager.getMultiplier(killer.getUniqueId());
        if (multiplier > 1.0 && event.getDroppedExp() > 0) {
            event.setDroppedExp((int) Math.round(event.getDroppedExp() * multiplier));
        }

        boolean allowPassive = plugin.getConfig().getBoolean("multiplier.allow-passive-mobs", false);
        if (!allowPassive && !(event.getEntity() instanceof Monster)) return;

        ItemStack offhand = killer.getInventory().getItemInOffHand();

        ItemStack migrated = charmManager.migrateToLedger(offhand);
        if (migrated != offhand) {
            offhand = migrated;
            killer.getInventory().setItemInOffHand(offhand);
        }

        if (!charmManager.isCharm(offhand) || charmManager.isFullyCharged(offhand)) return;

        int required      = plugin.getConfig().getInt("multiplier.charge-required", 100);
        int chargePerKill = plugin.getConfig().getInt("multiplier.mob-kill-charge", 1);
        int newCharge     = Math.min(charmManager.getCharge(offhand) + chargePerKill, required);
        charmManager.setCharge(offhand, newCharge);
        killer.getInventory().setItemInOffHand(offhand);

        if (newCharge >= required) {
            killer.sendMessage("§6❖ §aYour Soul Bound Ledger is fully charged! Right-click to consume.");
        }
    }

    /**
     * Handles token application and fully-charged ledger consumption (offhand only).
     * Non-fully-charged ledgers are left uncancelled so the book UI can open freely —
     * PlayerEditBookEvent is the guard that prevents any text from being saved.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent event) {
        if (!plugin.getConfig().getBoolean("multiplier.enabled", true)) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        EquipmentSlot hand = event.getHand();
        if (hand == null || hand != EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        ItemStack migrated = charmManager.migrateToLedger(offhand);
        if (migrated != offhand) {
            offhand = migrated;
            player.getInventory().setItemInOffHand(offhand);
        }

        // ── Withdraw token ────────────────────────────────────────────────
        if (charmManager.isWithdrawToken(offhand)) {
            event.setCancelled(true);
            double maxMult  = plugin.getConfig().getDouble("multiplier.max-multiplier", 10.0);
            double current  = dataManager.getMultiplier(player.getUniqueId());
            double tokenAmt = charmManager.getTokenAmount(offhand);
            if (current >= maxMult) {
                player.sendMessage("§cYour XP multiplier is already at the maximum (§e" + fmt(maxMult) + "x§c).");
                return;
            }
            double newMult = Math.min(current + tokenAmt, maxMult);
            dataManager.setMultiplier(player.getUniqueId(), newMult);
            if (offhand.getAmount() > 1) {
                offhand.setAmount(offhand.getAmount() - 1);
                player.getInventory().setItemInOffHand(offhand);
            } else {
                player.getInventory().setItemInOffHand(null);
            }
            player.sendMessage("§6❖ §b+" + fmt(tokenAmt) + "x Multiplier Token applied! Your multiplier is now §e" + fmt(newMult) + "x§b.");
            return;
        }

        // ── Soul Bound Ledger ─────────────────────────────────────────────
        if (!charmManager.isCharm(offhand)) return;

        // Not fully charged: let the book UI open; PlayerEditBookEvent blocks any saves
        if (!charmManager.isFullyCharged(offhand)) return;

        // Fully charged: cancel and consume — book must not open in the same tick as consume
        event.setCancelled(true);
        event.setUseItemInHand(org.bukkit.event.Event.Result.DENY);

        double maxMult = plugin.getConfig().getDouble("multiplier.max-multiplier", 10.0);
        double current = dataManager.getMultiplier(player.getUniqueId());

        if (current >= maxMult) {
            player.sendMessage("§cYour XP multiplier is already at the maximum (§e" + fmt(maxMult) + "x§c).");
            return;
        }

        player.getInventory().setItemInOffHand(null);
        double newMult = Math.min(current + 0.5, maxMult);
        dataManager.setMultiplier(player.getUniqueId(), newMult);
        player.sendMessage("§6❖ §aSoul Bound Ledger consumed! Your multiplier is now §e" + fmt(newMult) + "x§a.");
    }

    /**
     * Prevents any text edits or signing from saving to the Soul Bound Ledger.
     * Cancelling this event keeps the item as WRITABLE_BOOK with all original
     * name, lore, and PDC data intact — it can never become a WRITTEN_BOOK.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        Player player = event.getPlayer();
        if (charmManager.isCharm(player.getInventory().getItemInMainHand()) ||
            charmManager.isCharm(player.getInventory().getItemInOffHand())) {
            event.setCancelled(true);
        }
    }

    // Formats 2.0 → "2", 1.5 → "1.5"
    static String fmt(double v) {
        return v % 1 == 0 ? String.valueOf((int) v) : String.format("%.1f", v);
    }
}
