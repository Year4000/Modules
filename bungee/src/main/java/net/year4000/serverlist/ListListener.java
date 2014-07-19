package net.year4000.serverlist;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.serverlist.messages.Message;
import net.year4000.serverlist.messages.MessageFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static com.ewized.utilities.bungee.util.MessageUtil.replaceColors;

public class ListListener implements Listener {
    private final LoadingCache<PingServer, ServerPing> ping = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(new Settings().getCache(), TimeUnit.SECONDS)
        .build(new CacheLoader<PingServer, ServerPing>() {
               @Override
               public ServerPing load(PingServer pingServer) throws Exception {
                   Settings config = new Settings();
                   ServerPing.PlayerInfo[] players;
                   String motd = replaceColors(config.getPrefix());
                   InetSocketAddress ip = pingServer.getConnection().getAddress();
                   Message locale = new Message(pingServer.getLocale(ip));
                   String message = MessageFactory.get().getMessage();

                   // Load the random messages top layer.
                   ServerList.debug("Server List Message: " + message);
                   motd += locale.get(message);

                   // Load the player bottom layer and player sample.
                   if (pingServer.getPlayer(ip) != null) {
                       // Bottom row is a player is found.
                       String motdPlayer = locale.get(config.getPlayer());
                       motd += motdPlayer.equals("") ? "" : " \n" + motdPlayer.replaceAll("\\{player\\}", pingServer.getPlayer(ip));

                       // Set the player's ping to the one in the config.
                       players = new ServerPing.PlayerInfo[config.getPlayers().size()];
                       for (int i = 0; i < players.length; i++) {
                           String line = config.getPlayers().get(i);
                           players[i] = new ServerPing.PlayerInfo(locale.get(line), "");
                       }
                   } else {
                       // Bottom row if no player is found.
                       motd += config.getNoPlayer().equals("") ? "" : " \n" + locale.get(config.getNoPlayer());

                       // Set the player's ping for players on the server.
                       ProxyServer proxy = ProxyServer.getInstance();
                       int playerCount = proxy.getOnlineCount();
                       players = new ServerPing.PlayerInfo[playerCount];

                       for (int i = 0; i < playerCount; i++) {
                           String line = proxy.getPlayers().toArray()[i].toString();
                           players[i] = new ServerPing.PlayerInfo(locale.get(line), "");
                       }
                   }

                   // Set the Ping Response
                   pingServer.getResponse().getPlayers().setSample(players);
                   pingServer.getResponse().setDescription(motd);

                   return pingServer.getResponse();
               }
           });

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxyServer.getInstance().getScheduler().schedule(DuckTape.get(), () -> {
            ProxiedPlayer player = event.getPlayer();
            PingServer.addPlayer(event.getPlayer());
            ServerList.debug("ServerList Info: " + player.getName() + " | " + player.getLocale().toString());
        }, 5, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onServerPing(ProxyPingEvent event) throws Exception {
        ServerPing server = ping.get(new PingServer(event.getConnection(), event.getResponse()));
        if (server != null) {
            event.setResponse(server);
        }
    }
}
