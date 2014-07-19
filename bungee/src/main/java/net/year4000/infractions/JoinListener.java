package net.year4000.infractions;

import com.ewized.utilities.bungee.util.MessageUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.year4000.ducktape.bungee.DuckTape;

import java.util.concurrent.TimeUnit;

public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer name = event.getPlayer();
        Player player = new Player(name);

        ProxyServer.getInstance().getScheduler().schedule(DuckTape.get(), () -> {
            Message locale = new Message(name);

            if (player.isBanned()) {
                name.disconnect(createMessage(
                    locale.get("login.banned"),
                    name,
                    player.getLastMessage()
                ));
            }
            else if (player.isLocked()) {
                String locked = String.format(locale.get("login.locked", player.getTime()));
                name.disconnect(createMessage(locked, name, player.getLastMessage()));
            }
        }, 2, TimeUnit.SECONDS);
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
        return MessageUtil.makeMessage(type + "\n" + message + "\n\n" + link);
    }
}
