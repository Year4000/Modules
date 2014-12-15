package net.year4000.serverlist;

import com.google.common.base.Ascii;
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
import net.year4000.utilities.bungee.MessageUtil;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static net.year4000.utilities.bungee.MessageUtil.replaceColors;

public class ListListener implements Listener {
    private static final int MOTD_MAX = 55; // 50 + 5 for indicator size
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
               int rawPrefixSize = motd.length() - Math.abs(MessageUtil.stripColors(motd).length() - motd.length());
               motd += Ascii.truncate(locale.get(message), MOTD_MAX - rawPrefixSize, "&7...");

               // Load the player bottom layer and player sample.
               if (pingServer.getPlayer(ip) != null) {
                   // Bottom row is a player is found.
                   String motdPlayer = locale.get(config.getPlayer());
                   motd += motdPlayer.equals("") ? "" : " \n" + trueLength(motdPlayer.replaceAll("\\{player\\}", pingServer.getPlayer(ip)));

                   // Set the player's ping to the one in the config.
                   players = new ServerPing.PlayerInfo[config.getPlayers().size()];
                   for (int i = 0; i < players.length; i++) {
                       String line = config.getPlayers().get(i);
                       players[i] = new ServerPing.PlayerInfo(locale.get(line), "");
                   }
               } else {
                   // Bottom row if no player is found.
                   motd += config.getNoPlayer().equals("") ? "" : " \n" + trueLength(locale.get(config.getNoPlayer()));

                   // Set the player's ping for players on the server.
                   players = pingServer.getResponse().getPlayers().getSample();

                   if (players != null && players.length == 0) {
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
               pingServer.getResponse().setDescription(MessageUtil.replaceColors(motd));

               return pingServer.getResponse();
            }

            /** Strip colors to find out proper char length */
            public String trueLength(String message) {
                int trunk = Math.abs(MessageUtil.stripColors(message).length() - message.length()) + MOTD_MAX;

                return Ascii.truncate(message, trunk, "&7...");
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

            // todo re-enable when lag is fixed
            /*if (server.getPlayers().getOnline() > 5) {
                ProxyPingEvent.AnimatedPing ping = new ProxyPingEvent.AnimatedPing();
                ping.setDelay(120);

                for (int i = 0; i < server.getPlayers().getOnline(); i += (new Random().nextInt(2) + 1)) {
                    int count = i == 0 ? server.getPlayers().getOnline() : i > server.getPlayers().getOnline() ? server.getPlayers().getOnline() : i;
                    ping.getResponses().add(new ServerPing(server.getVersion(), new ServerPing.Players(server.getPlayers().getMax(), count, server.getPlayers().getSample()), server.getDescription(), server.getFaviconObject()));
                }

                event.setAnimatedPing(ping);
            }*/
        }
    }
}
