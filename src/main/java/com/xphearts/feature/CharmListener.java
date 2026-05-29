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

        ItemStack offhand  = killer.getInventory().getItemInOffHand();
        ItemStack mainhand = killer.getInventory().getItemInMainHand();

        ItemStack charm;
        EquipmentSlot charmSlot;
        if (charmManager.isCharm(offhand)) {
            charm = offhand; charmSlot = EquipmentSlot.OFF_HAND;
        } else if (charmManager.isCharm(mainhand)) {
            charm = mainhand; charmSlot = EquipmentSlot.HAND;
        } else {
            return;
        }

        if (charmManager.isFullyCharged(charm)) return;

        int required      = plugin.getConfig().getInt("multiplier.charge-required", 100);
        int chargePerKill = plugin.getConfig().getInt("multiplier.mob-kill-charge", 1);
        int newCharge     = Math.min(charmManager.getCharge(charm) + chargePerKill, required);
        charmManager.setCharge(charm, newCharge);
        setHandItem(killer, charmSlot, charm);

        if (newCharge >= required) {
            killer.sendMessage("§6❖ §aYour Soul Bound Ledger is fully charged! Right-click to consume.");
        }
    }

    /** Handles token application and fully-charged ledger consumption (either hand). */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent event) {
        if (!plugin.getConfig().getBoolean("multiplier.enabled", true)) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        EquipmentSlot hand = event.getHand();
        if (hand == null) return;

        Player player = event.getPlayer();
        ItemStack heldItem = (hand == EquipmentSlot.HAND)
                ? player.getInventory().getItemInMainHand()
                : player.getInventory().getItemInOffHand();

        // ── Withdraw token ────────────────────────────────────────────────
        if (charmManager.isWithdrawToken(heldItem)) {
            event.setCancelled(true);
            double maxMult  = plugin.getConfig().getDouble("multiplier.max-multiplier", 10.0);
            double current  = dataManager.getMultiplier(player.getUniqueId());
            double tokenAmt = charmManager.getTokenAmount(heldItem);
            if (current >= maxMult) {
                player.sendMessage("§cYour XP multiplier is already at the maximum (§e" + fmt(maxMult) + "x§c).");
                return;
            }
            double newMult = Math.min(current + tokenAmt, maxMult);
            dataManager.setMultiplier(player.getUniqueId(), newMult);
            if (heldItem.getAmount() > 1) {
                heldItem.setAmount(heldItem.getAmount() - 1);
                setHandItem(player, hand, heldItem);
            } else {
                setHandItem(player, hand, null);
            }
            player.sendMessage("§6❖ §b+" + fmt(tokenAmt) + "x Multiplier Token applied! Your multiplier is now §e" + fmt(newMult) + "x§b.");
            return;
        }

        // ── Soul Bound Ledger ─────────────────────────────────────────────
        if (!charmManager.isCharm(heldItem)) return;

        // Always cancel: prevents vanilla WITHER_ROSE block-placement
        event.setCancelled(true);

        // Not fully charged: do nothing
        if (!charmManager.isFullyCharged(heldItem)) return;

        double maxMult = plugin.getConfig().getDouble("multiplier.max-multiplier", 10.0);
        double current = dataManager.getMultiplier(player.getUniqueId());

        if (current >= maxMult) {
            player.sendMessage("§cYour XP multiplier is already at the maximum (§e" + fmt(maxMult) + "x§c).");
            return;
        }

        setHandItem(player, hand, null);
        double newMult = Math.min(current + 0.5, maxMult);
        dataManager.setMultiplier(player.getUniqueId(), newMult);
        player.sendMessage("§6❖ §aSoul Bound Ledger consumed! Your multiplier is now §e" + fmt(newMult) + "x§a.");
    }

    private void setHandItem(Player player, EquipmentSlot hand, ItemStack item) {
        if (hand == EquipmentSlot.HAND) {
            player.getInventory().setItemInMainHand(item);
        } else {
            player.getInventory().setItemInOffHand(item);
        }
    }

    // Formats 2.0 → "2", 1.5 → "1.5"
    static String fmt(double v) {
        return v % 1 == 0 ? String.valueOf((int) v) : String.format("%.1f", v);
    }
}
