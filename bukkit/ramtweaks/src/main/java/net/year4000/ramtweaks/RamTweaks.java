/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.ramtweaks;

import lombok.Getter;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.module.ModuleInfo;
import org.bukkit.scheduler.BukkitTask;

@ModuleInfo(
    name = "RamTweaks",
    version = "1.4",
    description = "Improve Bukkit with a Module",
    authors = {"Year4000"}
)
public class RamTweaks extends BukkitModule {
    @Getter
    private static RamTweaks inst;
    private BukkitTask restartClock;
    private BukkitTask lowRam;

    public void load() {
        inst = this;
    }

    public void enable() {
        restartClock = new RestartClock().getTask();
        lowRam = new LowRam().getTask();

        registerCommand(RamCommands.class);
    }

    public void disable() {
        restartClock.cancel();
        lowRam.cancel();
    }
}
