package com.xphearts.command;

import com.xphearts.data.PlayerDataManager;
import com.xphearts.feature.CharmManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class WithdrawCommand implements CommandExecutor {

    private final PlayerDataManager dataManager;
    private final CharmManager charmManager;

    public WithdrawCommand(PlayerDataManager dataManager, CharmManager charmManager) {
        this.dataManager  = dataManager;
        this.charmManager = charmManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        double current = dataManager.getMultiplier(player.getUniqueId());

        if (current < 2.0) {
            player.sendMessage("§cYou need at least §e2.0x §cmultiplier to withdraw. "
                    + "You currently have §e" + fmt(current) + "x§c.");
            return true;
        }

        double newMult = current - 1.0;
        dataManager.setMultiplier(player.getUniqueId(), newMult);

        ItemStack token = charmManager.createWithdrawToken();
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(token);

        if (!overflow.isEmpty()) {
            // Inventory full — roll back the multiplier change
            dataManager.setMultiplier(player.getUniqueId(), current);
            player.sendMessage("§cYour inventory is full. Free up a slot and try again.");
            return true;
        }

        player.sendMessage("§6❖ §aWithdrew §e1.0x §amultiplier. "
                + "You now have §e" + fmt(newMult) + "x§a. "
                + "§7Place the token in your offhand and right-click to apply it.");
        return true;
    }

    private static String fmt(double v) {
        return v % 1 == 0 ? String.valueOf((int) v) : String.format("%.1f", v);
    }
}
