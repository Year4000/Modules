package net.year4000.infractions;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.utilities.bungee.BungeeLocale;

public class Message extends BungeeLocale {
    public Message(ProxiedPlayer player) {
        super(player);
        localeManager = MessageManager.get();
    }

    public Message(CommandSender sender) {
        super(sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null);
        localeManager = MessageManager.get();
    }
}
