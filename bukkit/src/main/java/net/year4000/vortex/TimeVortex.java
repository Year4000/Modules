package net.year4000.vortex;

import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ducktape.module.ModuleInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

import java.util.concurrent.TimeUnit;

import static net.year4000.utilities.bukkit.MessageUtil.replaceColors;

@ModuleInfo(
    name = "Vortex",
    version = "1.0",
    description = "The time vortex the space that connects lost players",
    authors = {"Year4000"}
)
@ModuleListeners({TimeVortex.class})
public class TimeVortex extends BukkitModule implements Listener {
    private static final String ewized = "c9c2b7fe-e2c1-4266-9556-aafccc0d1f13";

    /** Return true | false if the map is running. */
    private boolean isMapPlaying(World world) {
        return world.getEnvironment() == World.Environment.THE_END || world.getName().contains("vortex");
    }

    // System Things

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(PlayerInitialSpawnEvent event) {
        event.setSpawnLocation(new Location(event.getPlayer().getWorld(), -1.5, 89.5, 4.5));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(PlayerJoinEvent event) {
        event.setJoinMessage("");
        Player player = event.getPlayer();
        player.teleport(new Location(player.getWorld(), -1.5, 89.5, 4.5));
        player.setExp(1F);
        player.setTotalExperience(0);
        player.sendMessage("");
        player.sendMessage(replaceColors("&6You were lost in the time vortex."));
        player.sendMessage(replaceColors("&6Luckily this &eTime Capsule &6has found you."));
        player.sendMessage("");
        SchedulerUtil.runSync(() -> {
            player.sendMessage(replaceColors("&6Type &e/hub &6to return to your universe."));
            player.sendMessage("");
        }, 30, TimeUnit.SECONDS);
        Bukkit.getOnlinePlayers().forEach(entity -> {
            entity.hidePlayer(player);
            player.hidePlayer(entity);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(PlayerQuitEvent event) {
        event.setQuitMessage("");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(AsyncPlayerChatEvent event) {
        event.setCancelled(isMapPlaying(event.getPlayer().getWorld()));
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(FoodLevelChangeEvent event) {
        event.setCancelled(isMapPlaying(event.getEntity().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        boolean map = isMapPlaying(entity.getWorld());

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID && map) {
            entity.teleport(new Location(entity.getWorld(), -1.5, 89.5, 4.5));
        }

        event.setCancelled(map);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().getUniqueId().toString().equals(ewized)) {
            event.setCancelled(isMapPlaying(event.getPlayer().getWorld()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(PlayerPickupItemEvent event) {
        event.setCancelled(isMapPlaying(event.getPlayer().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(PlayerPickupExperienceEvent event) {
        event.setCancelled(isMapPlaying(event.getPlayer().getWorld()));
    }

    // World Things

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL && !event.getPlayer().getUniqueId().toString().equals(ewized)) {
            event.setCancelled(isMapPlaying(event.getPlayer().getWorld()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(PlayerInteractEntityEvent event) {
        event.setCancelled(isMapPlaying(event.getPlayer().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockBreakEvent event) {
        if (!event.getPlayer().getUniqueId().toString().equals(ewized)) {
            event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockPlaceEvent event) {
        if (!event.getPlayer().getUniqueId().toString().equals(ewized)) {
            event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockDamageEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockDispenseEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(EntityExplodeEvent event) {
        event.setCancelled(isMapPlaying(event.getLocation().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockFadeEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockBurnEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockGrowEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockFormEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockIgniteEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(EntityBlockFormEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(CreatureSpawnEvent event) {
        event.setCancelled(isMapPlaying(event.getEntity().getWorld()));
    }
}
