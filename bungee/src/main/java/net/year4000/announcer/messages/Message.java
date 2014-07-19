package net.year4000.announcer.messages;

import com.ewized.utilities.bungee.util.BungeeLocale;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Message extends BungeeLocale {
    public Message(ProxiedPlayer player) {
        super(player);
        this.localeManager = MessageManager.get();
    }
}
