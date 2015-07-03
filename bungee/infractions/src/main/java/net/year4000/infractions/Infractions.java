package net.year4000.infractions;

import lombok.Getter;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.bungee.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;

@ModuleInfo(
    name = "Infractions",
    version = "1.3",
    description = "Temp infractions until Account plugin is made.",
    authors = {"Year4000"}
)
@ModuleListeners({JoinListener.class})
public class Infractions extends BungeeModule {
    @Getter private static Infractions instance;
    @Getter private static FileStorage storage;

    public void load() {
        instance = this;
    }

    public void enable() {
        storage = new FileStorage();

        // Register Listeners
        new JoinListener();
        // Enable Commands
        registerCommand(Commands.class);
    }
}
