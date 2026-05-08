package com.xphearts.listener;

import com.xphearts.XPHearts;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

    private final XPHearts plugin;

    public PlayerListener(XPHearts plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.updateHealth(event.getPlayer());
    }

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event) {
        plugin.updateHealth(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        // Delay 1 tick so the respawn finishes before we set health
        plugin.getServer().getScheduler().runTaskLater(plugin,
                () -> plugin.updateHealth(event.getPlayer()), 1L);
    }
}
