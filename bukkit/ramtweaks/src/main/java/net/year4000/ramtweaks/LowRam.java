/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.ramtweaks;

import lombok.Getter;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ramtweaks.messages.ShutdownMessage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class LowRam implements Runnable {
    @Getter
    private BukkitTask task;

    public LowRam() {
        task = SchedulerUtil.repeatAsync(this, 1, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        Runtime runtime = Runtime.getRuntime();
        double max = Math.floor(runtime.maxMemory() / 1024.0 / 1024.0);
        double total = Math.floor(runtime.totalMemory() / 1024.0 / 1024.0);
        double free = Math.floor(runtime.freeMemory() / 1024.0 / 1024.0);

        // log the ram usage
        if (Settings.get().isShowRamStats()) {
            RamTweaks.log("Max Ram: %s || Total Ram: %s || Free Ram: %s", max, total, free);
        }

        // shutdown the server when ram is too low
        int limit = (int) max / 5; // example 2048mb / 4 = 512mb
        if (max - limit < total && free < limit) {
            RamTweaks.log("Server restarting as their is low ram.");

            // Print debug log things to console
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "serverinfo");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uptime");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunks");

            new ShutdownMessage(10);
            task.cancel();
        }
    }
}
