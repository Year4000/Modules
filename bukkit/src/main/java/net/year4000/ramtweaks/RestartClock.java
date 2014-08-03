package net.year4000.ramtweaks;

import lombok.Getter;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ramtweaks.messages.ShutdownMessage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class RestartClock implements Runnable {
    @Getter
    private BukkitTask task;

    public RestartClock() {
        task = SchedulerUtil.repeatSync(this, 10, TimeUnit.MINUTES);
    }

    public void run() {
        // Check if last player online
        if (Bukkit.getOnlinePlayers().length == 0) {
            new ShutdownMessage(10);
            task.cancel();
        }
    }
}
