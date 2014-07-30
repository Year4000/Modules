package net.year4000.servermenu;

import lombok.Getter;
import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.core.module.ModuleInfo;
import net.year4000.servermenu.menus.MenuManager;
import org.bukkit.Bukkit;

@ModuleInfo(
    name = "ServerMenu",
    version = "1.0",
    description = "The menu that lets you connect to the servers",
    authors = {"Year4000"}
)
@ModuleListeners({MenuListener.class, BungeeSender.class})
public class ServerMenu extends BukkitModule {
    @Getter
    private static ServerMenu inst;

    @Override
    public void load() {
        inst = this;
    }

    @Override
    public void enable() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(DuckTape.get(), () -> MenuManager.get().updateServers(), 0,  20 * 60);
    }
}
