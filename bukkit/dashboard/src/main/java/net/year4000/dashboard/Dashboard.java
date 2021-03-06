/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.dashboard;

import com.google.common.base.Ascii;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.AbstractBadgeManager;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.BadgeManager;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.MessagingChannel;
import net.year4000.utilities.sdk.API;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleInfo(
    name = "Dashboard",
    version = "1.0",
    description = "Give users a dashboard when in hubs",
    authors = {"Year4000"}
)
@ModuleListeners({Dashboard.Listeners.class})
public class Dashboard extends BukkitModule {
    // API
    private static API api = new API();

    // Shimmer
    private static Set<String> shimmer = ImmutableSet.of("3", "b", "8", "7", "2", "a", "4", "c", "5", "d", "6", "e", "1", "9");
    private static final String NAME = "Year4000";
    private static final String IP = "mc.year4000.net";
    private static Iterable<String> forever = Iterables.cycle(shimmer);
    private static Iterator<String> color;
    private static final String UNKNOWN = "unknown";
    @Setter
    private static String server = UNKNOWN;

    // Badges
    private static Set<String> colors = ImmutableSet.of("2", "3", "5", "6", "a", "b", "c", "d", "e");
    private static Iterator<String> random = Iterables.cycle(colors).iterator();
    private static Map<Player, String> nameColors = new WeakHashMap<>();
    private static Map<Player, Scoreboard> scoreboards = new HashMap<>();
    private static BadgeManager manager = new BadgeManager();
    private static LoadingCache<Class<?>, Map.Entry<String, String>> SHIMMERS = CacheBuilder.<Class<?>, Map.Entry<String, String>>newBuilder()
        .expireAfterAccess(1, TimeUnit.SECONDS)
        .build(new CacheLoader<Class<?>, Map.Entry<String, String>>() {
            @Override
            public Map.Entry<String, String> load(Class<?> dashboard) throws Exception {
                return new AbstractMap.SimpleImmutableEntry<>(color.next(), color.next());
            }
        });

    // Network
    public static MessagingChannel connector;
    @Setter
    private static AtomicInteger size = new AtomicInteger();

    @Override
    public void enable() {
        connector = MessagingChannel.get();
        color = forever.iterator();
        SchedulerUtil.repeatAsync(() -> {
            String header = getTabHeader();
            Bukkit.getOnlinePlayers().forEach(player -> {
                setTabListHeadFoot(player, header, getTabFooter());

                if (player.getScoreboard() != null && player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
                    player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(fcolor(ChatColor.BOLD, header));
                }
            });
        }, 1, TimeUnit.SECONDS);

        SchedulerUtil.repeatAsync(() -> {
            if (Bukkit.getOnlinePlayers().size() > 0) {
                try {
                    //int api = APIManager.getNetworkPlayerCount().getOnline();
                    int api = 0;

                    if (size.get() != api) {
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            Scoreboard scoreboard = scoreboards.get(player);

                            if (scoreboard != null) {
                                createSidebar(player, scoreboard);
                            }
                        });
                    }

                    size.set(api);
                }
                catch (Exception e) {
                    Dashboard.debug(e, false);
                }
            }
        }, 2, TimeUnit.SECONDS);
    }

    public static String fcolor(ChatColor color, String message) {
        return MessageUtil.replaceColors(message.replaceAll(ChatColor.COLOR_CHAR + "([0-9a-fA-F])", "&$1" + color.toString()));
    }

    public static void createSidebar(Player player, Scoreboard scoreboard) {
        AbstractBadgeManager.Badges badge = manager.findBadge(player);
        String badgeName = badge.name().substring(0, 1) + badge.name().toLowerCase().substring(1);

        api.getAccountAsync(player.getUniqueId().toString(), (data, error) -> {
            SidebarManager sidebar = new SidebarManager();

            sidebar.addBlank();
            //sidebar.addLine("&6Online&7: &a" + size.get());
            sidebar.addLine("&6Rank&7: " + manager.getBadge(player) + " " + badge.getColor() + badgeName);
            sidebar.addLine("&6Credits&7: &a" + data.getCredits());
            sidebar.addLine("&6Tokens&7: &a" + data.getRawResponse().get("tokens").getAsString());
            sidebar.addLine("&6Level&7: &a" + data.getRawResponse().get("level").getAsString());
            sidebar.addLine("&6Experience&7: &a" + data.getRawResponse().get("experience").getAsString());
            sidebar.addBlank();
            sidebar.addLine(" &bwww&3.&byear4000&3.&bnet ");

            sidebar.buildSidebar(scoreboard, fcolor(ChatColor.BOLD, getTabHeader()));
        });
    }

    public static Team createUpdateTeam(Player player, Scoreboard scoreboard) {
        String teamId = "tab:" + (BadgeManager.MAX_RANK - manager.findBadge(player).getRank()) + (chars(player.getName()) >> 4);
        Team team = scoreboard.getTeam(teamId) == null ? scoreboard.registerNewTeam(teamId) : scoreboard.getTeam(teamId);

        // Team settings
        nameColors.putIfAbsent(player, random.next());
        String color = "&" + nameColors.get(player);
        String badge = manager.getBadge(player) + " " + color;
        team.setPrefix(MessageUtil.replaceColors(color));
        team.setSuffix("&f");
        team.addEntry(player.getName());

        // Player settings
        player.setDisplayName(MessageUtil.replaceColors(color + player.getName() + "&f"));
        player.setPlayerListName(MessageUtil.replaceColors(badge) + player.getName());

        return team;
    }

    public static String getTabFooter() {
        String ip = "&b" + IP.replaceAll("\\.", "&3.&b");

        return MessageUtil.replaceColors("&b" + server + " &7- " + ip);
    }

    public static String getTabHeader() {
        String b = "&" + SHIMMERS.getUnchecked(Dashboard.class).getKey();
        String name = b + "[&" + SHIMMERS.getUnchecked(Dashboard.class).getValue() + NAME + b + "]";

        return MessageUtil.replaceColors(name);
    }

    /** Generate strings to ascii code */
    public static int chars(String string) {
        String finalString = "";

        for (int i = 0; i < string.toCharArray().length ; i++) {
            if (finalString.length() > 6) break;

            finalString += (int) Ascii.toUpperCase(string.toCharArray()[i]);
        }

        return Integer.valueOf(finalString);
    }

    /** Set the tablist header and footer */
    public static void setTabListHeadFoot(Player player, String header, String footer) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        IChatBaseComponent headTitle = IChatBaseComponent.ChatSerializer.a(sanitize(net.year4000.utilities.MessageUtil.replaceColors(header)));
        IChatBaseComponent footTitle = IChatBaseComponent.ChatSerializer.a(sanitize(net.year4000.utilities.MessageUtil.replaceColors(footer)));
        PacketPlayOutPlayerListHeaderFooter headFoot = new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field head = headFoot.getClass().getDeclaredField("a");
            head.setAccessible(true);
            head.set(headFoot, headTitle);
            Field foot = headFoot.getClass().getDeclaredField("b");
            foot.setAccessible(true);
            foot.set(headFoot, footTitle);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {}

        craftPlayer.getHandle().playerConnection.sendPacket(headFoot);
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

    /** The Listeners that aid the dashboard */
    public static class Listeners implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();

            if (!scoreboards.containsKey(player)) {
                scoreboards.put(player, Bukkit.getScoreboardManager().getNewScoreboard());
            }

            // Scoreboard things
            Scoreboard scoreboard = scoreboards.get(player);
            player.setScoreboard(scoreboard);
            Bukkit.getOnlinePlayers().forEach(other -> {
                Scoreboard otherScoreboard = scoreboards.get(other);
                createUpdateTeam(other, scoreboard);
                createUpdateTeam(player, otherScoreboard);
            });
            createSidebar(player, scoreboard);

            // Scoreboard things
            SchedulerUtil.runSync(() -> {
                Bukkit.getOnlinePlayers().forEach(other -> {
                    Scoreboard otherScoreboard = scoreboards.get(other);
                    createUpdateTeam(other, scoreboard);
                    createUpdateTeam(player, otherScoreboard);
                });
                createSidebar(player, scoreboard);
            }, 1500, TimeUnit.MILLISECONDS);

            // Other
            setTabListHeadFoot(player, getTabHeader(), getTabFooter());

            // Get Server name
            if (server.equals(UNKNOWN)) {
                String[] header = new String[] {"GetServer"};

                SchedulerUtil.runSync(() -> Dashboard.connector.send(header, (data, error) -> {
                    if (error == null) {
                        Dashboard.setServer(data.readUTF());
                    }
                }), 2, TimeUnit.SECONDS);
            }
        }
    }
}
