/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.hub;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.List;

public class WorldListener implements Listener {
    private List<Chunk> newChunks = new ArrayList<>();

    /** Don't unload chunks its a hub */
    @EventHandler(ignoreCancelled = true)
    public void onWorldEvent(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }

    /** When a chunk populates move players back to spawn */
    @EventHandler(ignoreCancelled = true)
    public void onWorldEvent(ChunkPopulateEvent event) {
        // add chunk to new chunks list
        newChunks.add(event.getChunk());

        // move entities
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof Player) {
                entity.teleport(Hub.hubSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                ((Player) event).setGameMode(Hub.GAME_MODE);
            }
        }
    }

    /** Move players back to spawn */
    @EventHandler(ignoreCancelled = true)
    public void onWorldEvent(PlayerMoveEvent event) {
        int y = event.getTo().getBlockY();
        
        if (newChunks.contains(event.getTo().getChunk()) || y < 0) {
            event.getPlayer().teleport(Hub.hubSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            event.getPlayer().setGameMode(Hub.GAME_MODE);
        }
    }

    /** Protest the area around spawn by explosions */
    @EventHandler
    public void onProtectSpawn(EntityExplodeEvent event) {
        Location spawn = event.getLocation().getWorld().getSpawnLocation();
        Location pos = event.getLocation();

        if (pos.distance(spawn) < Hub.SPAWN_PROTECTION) {
            event.setCancelled(true);
        }
    }

    /** Protest the area around spawn by explosions */
    @EventHandler
    public void onProtectSpawn(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        Location spawn = event.getClickedBlock().getLocation().getWorld().getSpawnLocation();
        Location pos = event.getClickedBlock().getLocation();

        if (pos.distance(spawn) < Hub.SPAWN_PROTECTION && HubListener.mode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }
}
