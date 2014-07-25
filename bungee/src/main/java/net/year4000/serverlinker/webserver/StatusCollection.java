package net.year4000.serverlinker.webserver;

import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.year4000.ducktape.bungee.DuckTape;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

@Data
public class StatusCollection {
    private ProxyServer proxy = ProxyServer.getInstance();
    private static StatusCollection inst;
    // using this map as we need concurrency that is sorted
    private Map<String, ServerStatus> servers = new ConcurrentSkipListMap<>();

    public static StatusCollection get() {
        if (inst == null) {
            inst = new StatusCollection();
        }

        return inst;
    }

    public StatusCollection() {
        proxy.getServers().values().forEach(this::addServer);
    }

    public void updateStatus() {
        servers.forEach((name, server) -> server.ping());
    }

    public synchronized ServerStatus addServer(ServerInfo server) {
        return servers.put(server.getName(), new ServerStatus(server));
    }

    public synchronized  ServerStatus removeServer(ServerInfo server) {
        return servers.remove(server.getName());
    }

    public ScheduledTask updateClock() {
        return proxy.getScheduler().schedule(DuckTape.get(), this::updateStatus, 5, 5, TimeUnit.SECONDS);
    }
}
