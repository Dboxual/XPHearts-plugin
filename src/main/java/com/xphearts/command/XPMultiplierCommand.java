package com.xphearts.command;

import com.xphearts.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XPMultiplierCommand implements CommandExecutor {

    private final PlayerDataManager dataManager;

    public XPMultiplierCommand(PlayerDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§eConsole usage: /xpmultiplier set <player> <amount> | reset <player>");
                return true;
            }
            double mult = dataManager.getMultiplier(player.getUniqueId());
            player.sendMessage("§7Your XP multiplier: §a" + fmt(mult) + "x");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set" -> {
                if (!sender.hasPermission("xphearts.admin")) {
                    sender.sendMessage("§cNo permission.");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage("§eUsage: /xpmultiplier set <player> <amount>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage("§cPlayer not found: §7" + args[1]); return true; }
                try {
                    double amount = Double.parseDouble(args[2]);
                    if (amount < 1.0) { sender.sendMessage("§cAmount must be at least 1."); return true; }
                    dataManager.setMultiplier(target.getUniqueId(), amount);
                    sender.sendMessage("§aSet §e" + target.getName() + "§a's multiplier to §e" + fmt(amount) + "x§a.");
                    if (!target.equals(sender)) target.sendMessage("§7Your XP multiplier was set to §e" + fmt(amount) + "x §7by an admin.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cNot a valid number: §7" + args[2]);
                }
            }
            case "reset" -> {
                if (!sender.hasPermission("xphearts.admin")) {
                    sender.sendMessage("§cNo permission.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§eUsage: /xpmultiplier reset <player>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage("§cPlayer not found: §7" + args[1]); return true; }
                dataManager.resetMultiplier(target.getUniqueId());
                sender.sendMessage("§aReset §e" + target.getName() + "§a's multiplier to §e1x§a.");
                if (!target.equals(sender)) target.sendMessage("§7Your XP multiplier was reset to §e1x §7by an admin.");
            }
            default -> sender.sendMessage("§eUsage: /xpmultiplier [set <player> <amount> | reset <player>]");
        }
        return true;
    }

    private static String fmt(double v) {
        return v % 1 == 0 ? String.valueOf((int) v) : String.format("%.1f", v);
    }
}
