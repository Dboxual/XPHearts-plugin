package com.xphearts.feature;

import com.xphearts.XPHearts;
import com.xphearts.data.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
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

        // Apply XP multiplier to dropped XP
        int multiplier = dataManager.getMultiplier(killer.getUniqueId());
        if (multiplier > 1 && event.getDroppedExp() > 0) {
            event.setDroppedExp(event.getDroppedExp() * multiplier);
        }

        // Charge charm in offhand
        ItemStack offhand = killer.getInventory().getItemInOffHand();
        if (!charmManager.isCharm(offhand) || charmManager.isFullyCharged(offhand)) return;

        int required      = plugin.getConfig().getInt("multiplier.charge-required", 100);
        int chargePerKill = plugin.getConfig().getInt("multiplier.mob-kill-charge", 1);
        int newCharge     = Math.min(charmManager.getCharge(offhand) + chargePerKill, required);
        charmManager.setCharge(offhand, newCharge);
        killer.getInventory().setItemInOffHand(offhand);

        if (newCharge >= required) {
            killer.sendMessage("§6❖ §aYour XP Multiplier Charm is fully charged! Right-click to consume.");
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!plugin.getConfig().getBoolean("multiplier.enabled", true)) return;
        if (event.getHand() != EquipmentSlot.OFF_HAND) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player   = event.getPlayer();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        if (!charmManager.isCharm(offhand)) return;
        event.setCancelled(true); // always cancel interaction for charms

        if (!charmManager.isFullyCharged(offhand)) return;

        int maxMult = plugin.getConfig().getInt("multiplier.max-multiplier", 10);
        int current = dataManager.getMultiplier(player.getUniqueId());

        if (current >= maxMult) {
            player.sendMessage("§cYour XP multiplier is already at the maximum (§e" + maxMult + "x§c).");
            return;
        }

        // Consume charm and increase multiplier
        player.getInventory().setItemInOffHand(null);
        int newMult = current + 1;
        dataManager.setMultiplier(player.getUniqueId(), newMult);
        player.sendMessage("§6❖ §aXP Multiplier Charm consumed! Your multiplier is now §e" + newMult + "x§a.");
    }
}
