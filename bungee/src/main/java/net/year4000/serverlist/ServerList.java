package net.year4000.serverlist;

import lombok.Getter;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.bungee.module.ModuleListeners;
import net.year4000.ducktape.core.module.ModuleInfo;
import net.year4000.serverlist.commands.ListBase;

@ModuleInfo(
    name = "ServerList",
    version = "1.4",
    description = "Edit the way clients shows the server in the server-list.",
    authors = {"Year4000"}
)
@ModuleListeners({ListListener.class})
public class ServerList extends BungeeModule {
    @Getter
    private static ServerList inst;

    public void load() {
        inst = this;
    }

    public void enable() {
        registerCommand(ListBase.class);
    }
}
