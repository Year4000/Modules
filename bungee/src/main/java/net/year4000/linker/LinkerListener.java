package net.year4000.linker;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.year4000.utilities.bungee.MessageUtil;
import net.year4000.utilities.sdk.routes.players.PlayerCountJson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LinkerListener implements Listener {
    private List<ProxiedPlayer> login = new ArrayList<>();

    @EventHandler
    public void onServerSwith(ServerSwitchEvent event) {
        event.getPlayer().setTabHeader(MessageUtil.message("&3[&bYear4000&3]"), MessageUtil.message("&bmc&7.&byear4000&7.&bnet"));
        event.getPlayer().setReconnectServer(Linker.instance.getHub());
    }

    @EventHandler
    public void onConnectionLoss(ServerKickEvent event) {
        // Message from being kicked from server
        if (event.getState() == ServerKickEvent.State.CONNECTED) {
            BaseComponent[] serverName = MessageUtil.message(String.format(
                    "&6%s &7>> &f",
                    event.getPlayer().getServer().getInfo().getName()
            ));
            BaseComponent[] message = event.getKickReasonComponent();
            event.getPlayer().sendMessage(MessageUtil.merge(serverName, message));

            ServerInfo server = Linker.instance.getHub();
            if (server != null) {
                event.setCancelServer(server);
                event.setCancelled(true);
            }
            else {
                Linker.log("Their could be no online hub servers!");
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
        else if (event.getState() == ServerKickEvent.State.UNKNOWN) {
            event.setCancelServer(Linker.instance.getHub());
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

            ServerInfo server = Linker.instance.getHub();
            Linker.log(server.toString());

            if (server != null) {
                event.setTarget(server);
            }
            else {
                Linker.log("Their could be no online hub servers!");
            }

            login.remove(event.getPlayer());
        }
    }

    /** Change the player count depending on the players in the servers */
    @EventHandler(priority = 0)
    public void onServerPing(ProxyPingEvent event) throws Exception {
        ServerPing ping = event.getResponse();

        PlayerCountJson.Count playerCount = Linker.instance.api.getPlayerCount().getNetworkPlayerCount();

        ping.getPlayers().setOnline(playerCount.getOnline());
        ping.getPlayers().setMax(playerCount.getMax());
        List<ServerPing.PlayerInfo> sample = Linker.proxy.getPlayers().stream()
            .map(p -> new ServerPing.PlayerInfo(p.getDisplayName(), p.getUniqueId().toString()))
            .collect(Collectors.toList());

        int size = sample.size() > 100 ? 100 : sample.size();
        if (size > 0) {
            ping.getPlayers().setSample(sample.subList(0, size).toArray(new ServerPing.PlayerInfo[size]));
        }

        event.setResponse(ping);
    }
}
