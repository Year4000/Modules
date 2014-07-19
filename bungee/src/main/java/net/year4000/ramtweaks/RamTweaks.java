package net.year4000.ramtweaks;

import lombok.Getter;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.core.module.ModuleInfo;

@ModuleInfo(
    name = "RamTweaks",
    version = "1.3",
    description = "Improve BungeeCord with a Module",
    authors = {"Year4000"}
)
public class RamTweaks extends BungeeModule {
    @Getter
    private static RamTweaks inst;

    public void load() {
        inst = this;
    }

    public void enable() {
        registerCommand(RamCommands.class);
    }
}
