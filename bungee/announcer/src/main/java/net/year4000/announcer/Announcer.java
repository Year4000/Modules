/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.announcer;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.year4000.announcer.commands.AnnouncerBase;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.module.ModuleInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "Announcer",
    version = "1.6",
    description = "Broadcast messages to the servers",
    authors = {"Year4000"}
)
@Getter
public class Announcer extends BungeeModule {
    @Getter
    private static Announcer inst;
    private List<ScheduledTask> broadcasters = new ArrayList<>();

    @Override
    public void load() {
        inst = this;
    }

    @Override
    public void enable() {
        // Register the loop for each server.
        addSchedulers();

        // Register the commands
        registerCommand(AnnouncerBase.class);
    }

    @Override
    public void disable() {
        broadcasters.forEach(ScheduledTask::cancel);
    }

    /** Add the schedulers for the broadcast tasks. */
    public void addSchedulers() {
        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
            String name = server.getName();
            debug("Registering the server: %s", name);
            broadcasters.add(ProxyServer.getInstance().getScheduler().schedule(
                DuckTape.get(),
                new Broadcaster(name),
                Settings.get().getDelay(),
                Settings.get().getDelay(),
                TimeUnit.SECONDS
            ));
        }
    }

    /** Reload the schedulers. */
    public void reloadSchedulers() {
        for (ScheduledTask task : broadcasters) {
            debug("Stopping broadcast task: %s", task.getId());
            task.cancel();
        }

        addSchedulers();
    }
}
