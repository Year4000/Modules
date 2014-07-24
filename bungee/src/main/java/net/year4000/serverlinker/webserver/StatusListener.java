package net.year4000.serverlinker.webserver;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.year4000.serverlinker.ServerLinker;

import java.io.IOException;

// TODO MAKE BETTER
public final class StatusListener implements Listener {

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ServerInfo server = event.getPlayer().getServer().getInfo();

        ServerStatus status = StatusCollection.get().getServers().get(server.getName());

        if (status != null) {
            try {
                status.ping();
            } catch (IOException e) {
                ServerLinker.debug(e, false);
            }
        }
    }

    @EventHandler
    public void onKick(ServerKickEvent event) {
        ServerInfo server = event.getPlayer().getServer().getInfo();

        ServerStatus status = StatusCollection.get().getServers().get(server.getName());

        if (status != null) {
            try {
                status.ping();
            } catch (IOException e) {
                ServerLinker.debug(e, false);
            }
        }
    }

    @EventHandler
    public void onDisconnect(ServerDisconnectEvent event) {
        ServerInfo server = event.getPlayer().getServer().getInfo();

        ServerStatus status = StatusCollection.get().getServers().get(server.getName());

        if (status != null) {
            try {
                status.ping();
            } catch (IOException e) {
                ServerLinker.debug(e, false);
            }
        }
    }
}
