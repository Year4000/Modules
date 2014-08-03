package net.year4000.ramtweaks;

import lombok.Getter;
import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ramtweaks.messages.ShutdownMessage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class LowRam implements Runnable {
    private Runtime runtime = Runtime.getRuntime();
    @Getter
    private BukkitTask task;

    public LowRam() {
        task = SchedulerUtil.repeatSync(this, 1, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        double max = Math.floor(runtime.maxMemory() / 1024.0 / 1024.0);
        double total = Math.floor(runtime.totalMemory() / 1024.0 / 1024.0);
        double free = Math.floor(runtime.freeMemory() / 1024.0 / 1024.0);

        // log the ram usage
        Level last = DuckTape.get().getLog().getLevel();
        DuckTape.get().getLog().setLevel(Level.FINEST);
        RamTweaks.debug("Max Ram: %s | Total Ram: %s | Free Ram: %s", max, total, free);
        DuckTape.get().getLog().setLevel(last);

        // shutdown the server when ram is too low
        if (Math.abs(max - total) < 256 && free < 256) {
            RamTweaks.log("Server restarting as their is low ram.");

            // Print debug log things to console
            //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "debug info");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uptime");
            //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunks");

            new ShutdownMessage(10);
        }
    }
}
