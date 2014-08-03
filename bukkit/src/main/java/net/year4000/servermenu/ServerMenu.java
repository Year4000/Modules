package net.year4000.servermenu;

import lombok.Getter;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.servermenu.menus.InvMenu;
import net.year4000.servermenu.menus.MenuManager;

import java.util.concurrent.TimeUnit;

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
        SchedulerUtil.repeatAsync(() -> MenuManager.get().updateServers(), 5, TimeUnit.MINUTES);
        SchedulerUtil.repeatAsync(() -> MenuManager.get().getMenus().values().parallelStream().forEach(InvMenu::updateServers), 5, TimeUnit.SECONDS);
    }
}
