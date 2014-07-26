package net.year4000.hub;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import net.year4000.ducktape.bukkit.DuckTape;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public final class HubListener implements Listener {

    /** Should the event be cancled based on the player's mode */
    private boolean mode(Player player) {
        return !player.getGameMode().equals(GameMode.CREATIVE) || !player.isOp();
    }
    private boolean mode(Entity player) {
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
        Bukkit.getScheduler().runTaskLater(DuckTape.get(), () -> {
            FunEffectsUtil.playSound(e.getPlayer(), Sound.ENDERMAN_TELEPORT);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Math.sqrt(player.getLocation().distanceSquared(to)) < 50) {
                    player.playEffect(to, Effect.ENDER_SIGNAL, 1);
                }
            }
        }, 1);
    }

    // player login / leave //

    private void kit(Player player) {
        // Spawn Effects

        player.setGameMode(Hub.GAME_MODE);

        new EffectsClock(player);
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
    public void onLogin(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    // Effects clock //

    class EffectsClock implements Runnable {
        BukkitTask task;
        Player player;
        PotionEffectType[] types = {
            PotionEffectType.NIGHT_VISION,
            PotionEffectType.JUMP,
            PotionEffectType.SPEED
        };

        public EffectsClock(Player runon) {
            player = runon;
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(DuckTape.get(), this, 1, 60 * 20L);
        }

        @Override
        public void run() {
            if (!player.isOnline()) {
                task.cancel();
                return;
            }

            for (PotionEffectType type : types) {
                player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, 2, true));
            }
        }
    }
}
