package net.year4000.announcer;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.year4000.announcer.commands.AnnouncerBase;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.core.module.ModuleInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "Announcer",
    version = "1.4",
    description = "Broadcast messages to the servers",
    authors = {"Year4000"}
)
@Getter
public class Announcer extends BungeeModule {
    @Getter
    private static Announcer inst;
    private Settings settings;
    private Map<String, Integer> messagesIndex = new HashMap<>();
    private List<ScheduledTask> broadcasters = new ArrayList<>();

    public void load() {
        inst = this;
    }

    public void enable() {
        settings = new Settings();

        // Register the loop for each server.
        addSchedulers();

        // Register the commands
        registerCommand(AnnouncerBase.class);
    }

    public void disable() {
        messagesIndex.clear();
        broadcasters.forEach(ScheduledTask::cancel);
    }

    /**
     * Get the current index of the message.
     * @param server The server to get the index list.
     * @return The index position.
     */
    public int getMessageIndex(String server) {
        return messagesIndex.get(server);
    }

    /**
     * Set the index of the server.
     * @param server The server to change.
     * @param index  The index to set to.
     */
    public int setMessagesIndex(String server, int index) {
        messagesIndex.put(server, index);
        return getMessageIndex(server);
    }

    /**
     * Reload the config.
     * @return Config instance.
     */
    public Settings reloadConfig() {
        settings = new Settings();
        return settings;
    }

    /** Add the schedulers for the broadcast tasks. */
    public void addSchedulers() {
        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
            debug("Registering the server: %s", server.getName());
            setMessagesIndex(server.getName(), 0);
            broadcasters.add(ProxyServer.getInstance().getScheduler().schedule(
                DuckTape.get(),
                new Broadcaster(server.getName()),
                settings.getDelay(),
                settings.getDelay(),
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
