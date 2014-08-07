package net.year4000.serverlinker;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.year4000.serverlinker.webserver.StatusCollection;
import net.year4000.utilities.bungee.MessageUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class Listeners implements Listener {
    private List<ProxiedPlayer> login = new ArrayList<>();

    @EventHandler
    public void onConnectionLoss(ServerKickEvent event) {
        if (event.getCancelServer() == null) return;

        // Message from being kicked from server
        if (event.getState() == ServerKickEvent.State.CONNECTED) {
            BaseComponent[] serverName = MessageUtil.message(String.format(
                "&6%s &7>> &f",
                event.getPlayer().getServer().getInfo().getName()
            ));
            BaseComponent[] message = event.getKickReasonComponent();
            event.getPlayer().sendMessage(MessageUtil.merge(serverName, message));

            ServerInfo server = ServerLinker.getInstance().getLowestHub();
            if (server != null) {
                event.setCancelServer(server);
                event.setCancelled(true);
            }
            else {
                ServerLinker.log("Their could be no online hub servers!");
            }
        }
        // Message while connecting to server.
        else if (event.getState() == ServerKickEvent.State.CONNECTING) {
            BaseComponent[] server = MessageUtil.message(String.format(
                "&6%s &7>> &f",
                event.getPlayer().getName()
            ));
            BaseComponent[] message = event.getKickReasonComponent();
            event.getPlayer().sendMessage(MessageUtil.merge(server, message));
            event.setCancelServer(event.getPlayer().getServer().getInfo());
            event.setCancelled(true);
        }
    }

    /** When player is allowed to connect to a server add them to the list. */
    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        login.add(event.getPlayer());
    }

    /**
     * After they can connect, check is the sever login is a first join and send them
     * to the lowest online hub server.
     */
    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (login.contains(event.getPlayer())) {

            ServerInfo server = ServerLinker.getInstance().getLowestHub();
            if (server != null) {
                event.setTarget(server);
            }
            else {
                ServerLinker.log("Their could be no online hub servers!");
            }

            login.remove(event.getPlayer());
        }
    }

    /** Change the player count depending on the players in the servers */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerPing(ProxyPingEvent event) throws Exception {
        ServerPing ping = event.getResponse();

        int online = StatusCollection.get().getOnlinePlayers();

        ping.getPlayers().setOnline(online);
        ping.getPlayers().setMax(StatusCollection.get().getMaxPlayers());
        List<ServerPing.PlayerInfo> sample = StatusCollection.get().getSamplePlayers();
        int size = sample.size() > Settings.get().getSampleSize() ? Settings.get().getSampleSize() : sample.size();

        if (size > 0) {
            ping.getPlayers().setSample(sample.subList(0, size).toArray(new ServerPing.PlayerInfo[size]));
        }

        event.setResponse(ping);
    }
}