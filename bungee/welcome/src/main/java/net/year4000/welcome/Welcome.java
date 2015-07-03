/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.welcome;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.event.EventHandler;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.bungee.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "Welcome",
    version = "1.0",
    description = "Welcomes users to Year4000",
    authors = {"Year4000"}
)
@ModuleListeners({Welcome.WelcomeListener.class})
public class Welcome extends BungeeModule {
    private static final TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();
    private static PlayerMessage message;

    @Override
    public void enable() {
        message = new PlayerMessage();
    }

    /** Add the player so they can get the login message */
    public static class WelcomeListener implements Listener {
        @EventHandler
        public void onJoin(PostLoginEvent event) {
            message.addPlayer(event.getPlayer());
        }
    }

    /** Loop that keeps track of the players */
    private class PlayerMessage implements Runnable {
        private Deque<ProxiedPlayer> players = new ConcurrentLinkedDeque<>();

        PlayerMessage() {
            scheduler.schedule(DuckTape.get(), this, 250, 250, TimeUnit.MILLISECONDS);
        }

        public void addPlayer(ProxiedPlayer player) {
            players.add(player);
        }

        @Override
        public void run() {
            if (players.size() == 0) return;

            ProxiedPlayer player = players.pop();

            if (player == null || player.getLocale() == null) {
                players.add(player);
                return;
            }

            String locale = player.getLocale().toString();

            new WelcomeMessages(player).getMotd(locale).forEach(player::sendMessage);
        }
    }
}
