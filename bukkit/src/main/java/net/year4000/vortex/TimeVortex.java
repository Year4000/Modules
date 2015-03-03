package net.year4000.vortex;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.minecraft.server.v1_7_R4.Packet;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.bukkit.BadgeManager;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.spigotmc.ProtocolInjector;

import java.util.Iterator;
import java.util.Set;
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
    private static Set<String> shimmer = ImmutableSet.of("3", "b", "8", "7", "2", "a", "4", "c", "5", "d", "6", "e", "1", "9");
    private static final String NAME = "Year4000";
    private static final String IP = "mc.year4000.net";
    private static Iterable<String> forever = Iterables.cycle(shimmer);
    private static Iterator<String> color;
    private static final BadgeManager manager = new BadgeManager();
    private static Scoreboard scoreboard;
    private static final String ewized = "c9c2b7fe-e2c1-4266-9556-aafccc0d1f13";
    private static final double X = 13.5;
    private static final double Y = 90.2;
    private static final double Z = 7.5;
    private static final int YAW = 90;
    private static final int PITCH = 0;

    @Override
    public void enable() {
        color = forever.iterator();
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        SchedulerUtil.repeatAsync(() -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                setTabListHeadFoot(player, getTabHeader(), getTabFooter());
            });
        }, 1, TimeUnit.SECONDS);
    }

    /** Set the tablist header and footer */
    public static void setTabListHeadFoot(Player player, String header, String footer) {
        if (!isEight(player)) return;

        CraftPlayer craftPlayer = (CraftPlayer) player;

        Packet headFoot = new ProtocolInjector.PacketPlayOutPlayerListHeaderFooter(
            sanitize(header),
            sanitize(footer)
        );

        craftPlayer.getHandle().playerConnection.sendPacket(headFoot);
    }

    public static boolean isEight(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47;
    }

    /** Santize the string to be used in packets */
    public static String sanitize(String text) {
        if (text == null || text.length() == 0) {
            return "\"\"";
        }

        char c;
        int i;
        int len = text.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String t;
        sb.append('"');

        for (i = 0; i < len; i += 1) {
            c = text.charAt(i);

            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '/':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        t = "000" + Integer.toHexString(c);
                        sb.append("\\u" + t.substring(t.length() - 4));
                    }
                    else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    public static String getTabFooter() {
        String ip = "&b" + IP.replaceAll("\\.", "&3.&b");

        return MessageUtil.replaceColors(ip);
    }

    public static String getTabHeader() {
        String b = "&" + color.next();
        String name = b + "[&" + color.next() + NAME + b + "]";

        return MessageUtil.replaceColors(name);
    }

    /** Return true | false if the map is running. */
    private boolean isMapPlaying(World world) {
        return world.getEnvironment() == World.Environment.THE_END || world.getName().contains("vortex");
    }

    // System Things

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(PlayerInitialSpawnEvent event) {
        event.setSpawnLocation(new Location(event.getPlayer().getWorld(), X, Y, Z, YAW, PITCH));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(PlayerJoinEvent event) {
        event.setJoinMessage("");
        Player player = event.getPlayer();
        SchedulerUtil.runAsync(() -> {
            Team team = scoreboard.getTeam(player.getName()) == null ? scoreboard.registerNewTeam(player.getName()) : scoreboard.getTeam(player.getName());
            team.setPrefix(manager.getBadge(player) + replaceColors(" &b"));
            team.setSuffix(replaceColors("&f"));
            team.add(player.getName());
            player.setScoreboard(scoreboard);
        });
        player.teleport(new Location(player.getWorld(), X, Y, Z, YAW, PITCH));
        player.setExp(1F);
        player.setTotalExperience(0);
        player.sendMessage("");
        player.sendMessage(replaceColors("&6You were lost in the time vortex."));
        player.sendMessage(replaceColors("&6Luckily a &eTime Capsule &6has found you."));
        player.sendMessage("");
        SchedulerUtil.runAsync(() -> {
            player.sendMessage("");
            player.sendMessage(replaceColors("&6Type &e/hub &6to return to your universe."));
            player.sendMessage("");
        }, 20, TimeUnit.SECONDS);
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
            entity.teleport(new Location(entity.getWorld(), X, Y, Z, YAW, PITCH));
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
