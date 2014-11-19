package net.year4000.infractions;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.event.EventHandler;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.utilities.bungee.MessageUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class JoinListener implements Listener {
    private static List<ProxiedPlayer> pendingLocales = new CopyOnWriteArrayList<>();

    @EventHandler
    public void onConnecting(ServerConnectEvent event) {
        while (pendingLocales.contains(event.getPlayer()));
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();

        class CheckPlayer implements Runnable {
            ScheduledTask task;
            ProxiedPlayer player;

            CheckPlayer(ProxiedPlayer player) {
                Infractions.getStorage().saveUUID(player);
                this.player = player;
                task = scheduler.schedule(DuckTape.get(), this, 250, 250, TimeUnit.MILLISECONDS);
            }

            @Override
            public void run() {
                Player iplayer = new Player(player);

                if (iplayer.isBanned() || iplayer.isLocked()) {
                    pendingLocales.add(player);
                    if (player.getLocale() == null) return;

                    Message locale = new Message(player);

                    if (iplayer.isBanned()) {
                        player.disconnect(createMessage(
                            locale.get("login.banned"),
                            player,
                            iplayer.getLastMessage()
                        ));
                    } else if (iplayer.isLocked()) {
                        String locked = String.format(locale.get("login.locked", iplayer.getTime()));
                        player.disconnect(createMessage(locked, player, iplayer.getLastMessage()));
                    }
                }

                task.cancel();
            }
        }

        pendingLocales.remove(event.getPlayer());
        new CheckPlayer(event.getPlayer());
    }

    /**
     * Create a disconnect message to tell the user their account can't login.
     * @param type The message type.
     * @param player The player's name.
     * @param message The message to show.
     * @return Disconnect message.
     */
    private static BaseComponent[] createMessage(String type, ProxiedPlayer player, String message) {
        String link = Config.get().getLink().replaceAll("%player%", player.getName());
        return MessageUtil.message(type + "\n" + message + "\n\n" + link);
    }
}
