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
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.serverlist.messages.Message;
import net.year4000.serverlist.messages.MessageFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static net.year4000.utilities.bungee.MessageUtil.replaceColors;

public class ListListener implements Listener {
    private final LoadingCache<PingServer, ServerPing> ping = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(new Settings().getCache(), TimeUnit.SECONDS)
        .build(new CacheLoader<PingServer, ServerPing>() {
               @Override
               public ServerPing load(PingServer pingServer) throws Exception {
                   ProxyServer proxy = ProxyServer.getInstance();
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
                       players = pingServer.getResponse().getPlayers().getSample();

                       if (players.length == 0) {
                           int playerCount = proxy.getOnlineCount();
                           players = new ServerPing.PlayerInfo[playerCount];

                           for (int i = 0; i < playerCount; i++) {
                               String line = proxy.getPlayers().toArray()[i].toString();
                               players[i] = new ServerPing.PlayerInfo(line, "");
                           }
                       }
                   }

                   // Set the Ping Response
                   pingServer.getResponse().getPlayers().setSample(players);
                   pingServer.getResponse().setDescription(motd);

                   return pingServer.getResponse();
               }
           });

    @EventHandler
    public void onPostLogin(final PostLoginEvent event) {
        TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();

        class AddPlayer implements Runnable {
            ScheduledTask task;
            ProxiedPlayer player;

            AddPlayer(ProxiedPlayer player) {
                this.player = player;
                task = scheduler.schedule(DuckTape.get(), this, 1, 1, TimeUnit.SECONDS);
            }

            @Override
            public void run() {
                if (player.getLocale() == null) return;

                PingServer.addPlayer(player);
                ServerList.debug("ServerList Info: " + player.getName() + " | " + player.getLocale().toString());
                task.cancel();
            }
        }

        new AddPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerPing(ProxyPingEvent event) throws Exception {
        ServerPing server = ping.getUnchecked(new PingServer(event.getConnection(), event.getResponse()));
        if (server != null) {
            event.setResponse(server);
        }
    }
}
