package net.year4000.announcer.messages;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.utilities.bungee.BungeeLocale;

public class InMessage extends BungeeLocale {
    public InMessage(ProxiedPlayer player) {
        super(player);
        this.localeManager = InternalManager.get();
    }
}
