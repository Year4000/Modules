package net.year4000.ramtweaks;

import lombok.Getter;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.core.module.ModuleInfo;

@ModuleInfo(
    name = "RamTweaks",
    version = "1.4",
    description = "Improve BungeeCord with a Module",
    authors = {"Year4000"}
)
public class RamTweaks extends BungeeModule {
    @Getter
    private static RamTweaks inst;
    private ScheduledTask restartClock;

    public void load() {
        inst = this;
    }

    public void enable() {
        restartClock = new RestartClock().getTask();

        registerCommand(RamCommands.class);
    }

    public void disable() {
        restartClock.cancel();
    }
}
