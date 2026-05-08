package com.xphearts.command;

import com.xphearts.XPHearts;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XPHeartsCommand implements CommandExecutor {

    private final XPHearts plugin;

    public XPHeartsCommand(XPHearts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§eUsage: /xphearts <reload|check [player]>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(sender);
            case "check"  -> handleCheck(sender, args);
            default       -> sender.sendMessage("§eUsage: /xphearts <reload|check [player]>");
        }
        return true;
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("xphearts.reload")) {
            sender.sendMessage("§cYou don't have permission to do that.");
            return;
        }
        plugin.reloadFeatures();
        sender.sendMessage("§aXPHearts reloaded. Config applied, health updated for all online players.");
    }

    private void handleCheck(CommandSender sender, String[] args) {
        if (!sender.hasPermission("xphearts.check")) {
            sender.sendMessage("§cYou don't have permission to do that.");
            return;
        }

        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found: §7" + args[1]);
                return;
            }
        } else if (sender instanceof Player p) {
            target = p;
        } else {
            sender.sendMessage("§eUsage: /xphearts check <player>");
            return;
        }

        int level   = target.getLevel();
        int hearts  = (int) (plugin.calculateMaxHealth(level) / 2);
        int extra   = hearts - plugin.getConfig().getInt("base-hearts", 10);
        String sign = extra > 0 ? "+" : "";

        sender.sendMessage("§6" + target.getName()
                + " §8| §7Level §b" + level
                + " §8| §7Hearts §c" + hearts + "§7/§c" + plugin.getConfig().getInt("max-hearts", 20)
                + " §8(§a" + sign + extra + " extra§8)");
    }
}
