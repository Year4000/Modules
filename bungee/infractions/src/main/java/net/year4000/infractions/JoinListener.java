/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.infractions;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.year4000.utilities.bungee.MessageUtil;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class JoinListener implements Listener {
    @EventHandler(priority = Byte.MAX_VALUE)
    public void login(LoginEvent event) {
        try {
            String id = checkNotNull(event.getConnection().getUniqueId().toString());
            Optional<Player> player = Infractions.getStorage().getPlayer(id);

            if (!player.isPresent()) return;

            Player record = player.get();

            if (record.isBanned() || record.isLocked()) {
                event.setCancelled(true);
                Message locale = new Message(record.getLocale());

                if (record.isBanned()) {
                    BaseComponent[] msg = createMessage(locale.get("login.banned"), record.getName(), record.getLastMessage());
                    String message = TextComponent.toLegacyText(msg);
                    //System.out.println(message);
                    event.setCancelReason(message);
                }
                else if (record.isLocked()) {
                    String date = new Date(TimeUnit.SECONDS.toMillis(record.getTime())).toString();
                    String locked = String.format(locale.get("login.locked", date));
                    BaseComponent[] msg = createMessage(locked, record.getName(), record.getLastMessage());
                    String message = TextComponent.toLegacyText(msg);
                    //System.out.println(message);
                    event.setCancelReason(message);
                }
            }
        }
        catch (NullPointerException e) {
            event.setCancelled(true);
            event.setCancelReason(MessageUtil.replaceColors("&6Error connecting to API&7..."));
            e.printStackTrace();
        }
    }

    /**
     * Create a disconnect message to tell the user their account can't login.
     * @param type The message type.
     * @param player The player's name.
     * @param message The message to show.
     * @return Disconnect message.
     */
    private static BaseComponent[] createMessage(String type, String player, String message) {
        String link = Settings.get().getLink().replaceAll("%player%", player);
        return MessageUtil.message(type + "\n" + message + "\n\n" + link);
    }
}
