package net.year4000.hubbar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BarAPIListener implements Listener {
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        BarAPI.handleTeleport(event.getPlayer(), event.getTo());
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent event) {
        BarAPI.handleTeleport(event.getPlayer(), event.getRespawnLocation());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        BarAPI.removeBar(event.getPlayer());
    }
}
