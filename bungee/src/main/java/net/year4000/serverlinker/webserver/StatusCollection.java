package net.year4000.serverlinker.webserver;

import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.utilities.Pinger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

@Data
public class StatusCollection {
    private ProxyServer proxy = ProxyServer.getInstance();
    private static StatusCollection inst;
    // using this map as we need concurrency that is sorted
    private final Map<String, ServerStatus> servers = new ConcurrentSkipListMap<>();

    public static StatusCollection get() {
        if (inst == null) {
            inst = new StatusCollection();
        }

        return inst;
    }

    public StatusCollection() {
        proxy.getServers().values().forEach(this::addServer);
    }

    /** Don't display servers in the group hidden publicly */
    public Map<String, ServerStatus> getNonHiddenServers() {
        Map<String, ServerStatus> nonHidden = new TreeMap<>();

        servers.forEach((name, server) -> {
            if (!server.isHidden()) {
                nonHidden.put(name, server);
            }
        });

        return nonHidden;
    }

    /** Update the ping status of each server */
    public void updateStatus() {
        servers.values().forEach(ServerStatus::ping);
    }

    /** Add a server to be tracked by the api */
    public synchronized ServerStatus addServer(ServerInfo server) {
        return servers.put(server.getName(), new ServerStatus(server));
    }

    /** Remove a server to be tracked by the api */
    public synchronized ServerStatus removeServer(ServerInfo server) {
        return servers.remove(server.getName());
    }

    /** The clock that is ran to update the pings of the servers */
    public ScheduledTask updateClock() {
        return proxy.getScheduler().schedule(DuckTape.get(), this::updateStatus, 5, 2, TimeUnit.SECONDS);
    }

    /** Get the max players of all the servers combined */
    public int getMaxPlayers() {
        int size = 0;

        for (ServerStatus status : servers.values()) {
            if (status.getStatus() == null) continue;

            size += status.getStatus().getPlayers().getMax();
        }

        return size;
    }

    /** Get the max players of all the servers combined */
    public int getOnlinePlayers() {
        int size = 0;

        for (ServerStatus status : servers.values()) {
            if (status.getStatus() == null) continue;

            Pinger.Players players = status.getStatus().getPlayers();

            if (players.getSample() != null && players.getSample().size() > players.getOnline()) {
                size += players.getSample().size();
            }
            else {
                size += players.getOnline();
            }
        }

        return size;
    }

    /** Gets the list of PlayerInfo of all the servers */
    public List<ServerPing.PlayerInfo> getSamplePlayers() {
        final List<ServerPing.PlayerInfo> players = new ArrayList<>();

        servers.forEach((name, server) -> {
            if (server.getStatus() != null && server.getStatus().getPlayers().getSample() != null) {
                server.getStatus().getPlayers().getSample().forEach(player -> {
                    // the dash is to check if its a real minecraft player name and not a fake
                    if (player.getId().contains("-")) {
                        players.add(new ServerPing.PlayerInfo(player.getName(), player.getId()));
                    }
                });
            }
        });

        return players;
    }
}
