package net.year4000.bages;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "Badges",
    version = "1.0",
    description = "Show player's badges",
    authors = {"Year4000"}
)
@ModuleListeners({Badges.BadgeListener.class})
public class Badges extends BukkitModule {
    private static Set<String> colors = ImmutableSet.of("2", "3", "5", "6", "a", "b", "c", "d", "e");
    private static Iterator<String> random = Iterables.cycle(colors).iterator();
    private static Scoreboard scoreboard;
    private static BadgeManager manager = new BadgeManager();

    @Override
    public void enable() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public static class BadgeListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onJoin(PlayerJoinEvent event) {
            SchedulerUtil.runSync(() -> {
                Player player = event.getPlayer();

                try {
                    String uuid = player.getName();
                    Team team = scoreboard.getTeam(uuid);

                    // register team if not exists
                    if (team == null) {
                        scoreboard.registerNewTeam(uuid);
                        team = scoreboard.getTeam(uuid);
                    }

                    String playerColor = MessageUtil.replaceColors("&" + random.next());
                    player.setDisplayName(MessageUtil.replaceColors(playerColor + uuid + "&f"));
                    team.setPrefix(manager.getBadge(player) + " " + playerColor);
                    team.setSuffix(MessageUtil.replaceColors("&f"));
                    team.add(uuid);
                    player.setScoreboard(scoreboard);
                } catch (Exception e) {
                    player.kickPlayer(e.getMessage());
                }
            }, 1, TimeUnit.SECONDS);
        }
    }
}
