package net.year4000.ramtweaks;

import lombok.Getter;
import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.ramtweaks.messages.ShutdownMessage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class LowRam implements Runnable {
    private Runtime runtime = Runtime.getRuntime();
    @Getter
    private BukkitTask task;

    public LowRam() {
        task = Bukkit.getScheduler().runTaskTimer(DuckTape.get(), this, 20L * 60, 20L * 60);
    }

    @Override
    public void run() {
        double max = Math.floor(runtime.maxMemory() / 1024.0 / 1024.0);
        double total = Math.floor(runtime.totalMemory() / 1024.0 / 1024.0);
        double free = Math.floor(runtime.freeMemory() / 1024.0 / 1024.0);

        if (Math.abs(max - total) < 128 && free < 128) {
            RamTweaks.log("Server restarting as their is low ram.");

            // Print debug log things to console
            //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "debug info");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uptime");
            //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunks");

            new ShutdownMessage(10);
        }
    }
}
