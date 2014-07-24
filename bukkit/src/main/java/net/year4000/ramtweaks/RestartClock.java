package net.year4000.ramtweaks;

import lombok.Getter;
import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.ramtweaks.messages.ShutdownMessage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class RestartClock implements Runnable {
    @Getter
    private BukkitTask task;

    public RestartClock() {
        task = Bukkit.getScheduler().runTaskTimer(DuckTape.get(), this, 20 * 60 * 10, 20 * 60 * 10);
    }

    public void run() {
        // Check if last player online
        if (Bukkit.getOnlinePlayers().length == 0) {
            new ShutdownMessage(10);
            task.cancel();
        }
    }
}
