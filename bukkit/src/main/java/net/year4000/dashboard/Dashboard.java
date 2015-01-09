package net.year4000.dashboard;

import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.Packet;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.servermenu.ServerMenu;
import net.year4000.servermenu.menus.APIManager;
import net.year4000.servermenu.menus.ServerJson;
import net.year4000.utilities.AbstractBadgeManager;
import net.year4000.utilities.Pinger;
import net.year4000.utilities.bukkit.BadgeManager;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.MessagingChannel;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.spigotmc.ProtocolInjector;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@ModuleInfo(
    name = "Dashboard",
    version = "1.0",
    description = "Give users a dashboard when in hubs",
    authors = {"Year4000"}
)
@ModuleListeners({Dashboard.Listeners.class})
public class Dashboard extends BukkitModule {
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
                if (isEight(player)) {
                    setTabListHeadFoot(player, header, getTabFooter());
                }
                else {
                    String ip = "&b" + IP.replaceAll("\\.", "&3.&b");
                    BossBar.setMessage(player, header + MessageUtil.replaceColors(" &7- " + ip), 0.0001F);
                }

                player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(header);
            });
        }, 1, TimeUnit.SECONDS);

        SchedulerUtil.repeatAsync(() -> {
            if (Bukkit.getOnlinePlayers().size() > 0) {
                int api = 0;

                for (ServerJson s : APIManager.getServers()) {
                    Pinger.StatusResponse status = s.getStatus();

                    if (status != null && status.getPlayers().getSample() != null) {
                        api += status.getPlayers().getOnline() < status.getPlayers().getSample().size() ? status.getPlayers().getSample().size() : status.getPlayers().getOnline();
                    }
                    else if (status != null) {
                        api += status.getPlayers().getOnline();
                    }
                }

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
        }, 2, TimeUnit.SECONDS);
    }

    public static boolean isEight(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47;
    }

    public static void createSidebar(Player player, Scoreboard scoreboard) {
        SidebarManager sidebar = new SidebarManager();
        AbstractBadgeManager.Badges badge = manager.findBadge(player);
        String badgeName = badge.name().substring(0, 1) + badge.name().toLowerCase().substring(1);

        sidebar.addBlank();
        sidebar.addLine("&6Online&7: &a" + size.get());
        sidebar.addLine("&6Rank&7: " + manager.getBadge(player) + " " + badge.getColor() + badgeName);
        sidebar.addLine("&6Web&7: &bwww&3.&byear4000&3.&bnet");

        sidebar.buildSidebar(scoreboard, getTabHeader());
    }

    public static Team createUpdateTeam(Player player, Scoreboard scoreboard) {
        String[] split = split(player);
        String teamId = "tab:" + (BadgeManager.MAX_RANK - manager.findBadge(player).getRank()) + (chars(player.getName()) >> 4);
        Team team = scoreboard.getTeam(teamId) == null ? scoreboard.registerNewTeam(teamId) : scoreboard.getTeam(teamId);

        // Team settings
        nameColors.putIfAbsent(player, random.next());
        String color = "&" + nameColors.get(player);
        String badge = manager.getBadge(player) + " " + color;
        team.setPrefix(MessageUtil.replaceColors(color));
        team.setSuffix(MessageUtil.replaceColors(split[1] + "&f"));
        team.add(split[0]);

        // Player settings
        player.setDisplayName(MessageUtil.replaceColors(color + player.getName() + "&f"));
        player.setPlayerListName(split[0]);
        player.setPlayerListDisplayName(MessageUtil.replaceColors(badge) + player.getName());

        return team;
    }

    public static String[] split(Player player) {
        String name = player.getName();
        return new String[] {name.substring(0, name.length() - 2), name.substring(name.length() - 2)};
    }

    public static String getTabFooter() {
        String ip = "&b" + IP.replaceAll("\\.", "&3.&b");

        return MessageUtil.replaceColors("&b" + server + " &7- " + ip);
    }

    public static String getTabHeader() {
        String b = "&" + color.next();
        String name = b + "[&" + color.next() + NAME + b + "]";

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
        if (!isEight(player)) return;

        CraftPlayer craftPlayer = (CraftPlayer) player;

        Packet headFoot = new ProtocolInjector.PacketPlayOutPlayerListHeaderFooter(
            sanitize(header),
            sanitize(footer)
        );

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
            scoreboards.forEach(Dashboard::createUpdateTeam);
            SchedulerUtil.runSync(() -> scoreboards.forEach(Dashboard::createUpdateTeam), 1500, TimeUnit.MILLISECONDS);
            createSidebar(player, scoreboard);

            // Other
            if (isEight(player)) {
                setTabListHeadFoot(player, getTabHeader(), getTabFooter());
            }
            else {
                String ip = "&b" + IP.replaceAll("\\.", "&3.&b");
                BossBar.setMessage(player, getTabHeader() + MessageUtil.replaceColors(" &7- " + ip), 0.0001F);
            }

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
