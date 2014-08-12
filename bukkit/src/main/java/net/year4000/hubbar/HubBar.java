package net.year4000.hubbar;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "HubBar",
    version = "1.1",
    description = "Have a boss bar in the lobby to tell its Year4000",
    authors = {"Year4000"}
)
public class HubBar extends BukkitModule {
    private Set<String> shimmer = ImmutableSet.of("3", "b", "8", "7", "2", "a", "4", "c", "5", "d", "6", "e", "1", "9");
    private final String NAME = "Year4000";
    private final String IP = "mc.year4000.net";
    private Iterable<String> forever = Iterables.cycle(shimmer);
    private Iterator<String> color;
    private BukkitTask task;

    @Override
    public void enable() {
        color = forever.iterator();

        // the task that updates the bar
        task = SchedulerUtil.repeatSync(() -> {
            // change the message
            String b = "&" + color.next() + "&l";
            String name = b + "[&" + color.next() + "&l" + NAME + b + "]";
            String ip = "&b&l" + IP.replaceAll("\\.", "&3&l.&b&l");

            // send message
            for (Player player : Bukkit.getOnlinePlayers()) {
                BossBar.setMessage(player, MessageUtil.replaceColors(name + " &7| " + ip), 0.0001F);
            }
        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public void disable() {
        task.cancel();
    }
}
