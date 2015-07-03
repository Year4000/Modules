package net.year4000.serverlist;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.bungee.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.serverlist.commands.ListBase;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(
    name = "ServerList",
    version = "1.5",
    description = "Edit the way clients shows the server in the server-list.",
    authors = {"Year4000"}
)
@ModuleListeners({ListListener.class})
public class ServerList extends BungeeModule {
    @Getter
    private static ServerList inst;
    @Getter
    private List<ScheduledTask> tasks = new ArrayList<>();

    public void load() {
        inst = this;
    }

    public void enable() {
        registerCommand(ListBase.class);
    }


    public void disable() {
        tasks.stream().filter(t -> t != null).forEach(ProxyServer.getInstance().getScheduler()::cancel);
    }
}
