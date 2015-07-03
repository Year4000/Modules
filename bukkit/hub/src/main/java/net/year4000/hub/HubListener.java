package net.year4000.hub;

import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public final class HubListener implements Listener {
    /** Should the event be cancled based on the player's mode */
    public static boolean mode(Player player) {
        return !player.getGameMode().equals(GameMode.CREATIVE) || !player.isOp();
    }
    public static boolean mode(Entity player) {
        return player instanceof Player && mode((Player) player);
    }

    // player events //

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        event.setCancelled(mode(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(mode(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBreak(EntityDamageEvent event) {
        event.setCancelled(mode(event.getEntity()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        Location to = e.getTo().clone();

        // Center in block
        to.setX(to.getBlockX() >= 0 ? to.getBlockX() + 0.5 : to.getBlockX() - 0.5);
        to.setZ(to.getBlockZ() >= 0 ? to.getBlockZ() + 0.5 : to.getBlockZ() - 0.5);

        e.setTo(to);

        // Cool Effect
        SchedulerUtil.runSync(() -> {
            FunEffectsUtil.playSound(e.getPlayer(), Sound.ENDERMAN_TELEPORT);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Math.sqrt(player.getLocation().distanceSquared(to)) < 50) {
                    player.playEffect(to, Effect.ENDER_SIGNAL, 1);
                }
            }
        });
    }

    // player login / leave //

    private void kit(Player player) {
        player.setTotalExperience(0);
        player.setExp(0F);
        player.setGameMode(Hub.GAME_MODE);

        // Clear effects then set them
        player.getActivePotionEffects().forEach(p -> player.removePotionEffect(p.getType()));
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        event.getPlayer().teleport(Hub.hubSpawn());

        kit(event.getPlayer());
    }

    @EventHandler
    public void onLogin(PlayerRespawnEvent event) {
        event.setRespawnLocation(Hub.hubSpawn());

        kit(event.getPlayer());
    }

    @EventHandler
    public void onLogin(PlayerDeathEvent event) {
        event.getDrops().clear();
    }

    @EventHandler
    public void onLogin(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    /*@EventHandler
    public void closeMenu(InventoryClickEvent event) {
        if (event.getRawSlot() == 0 && event.getInventory().getType().equals(InventoryType.PLAYER)) {
            event.getWhoClicked().openWorkbench(event.getWhoClicked().getLocation(), true);
        }
    }*/

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        event.setCancelled(mode(event.getPlayer()));
    }
}
