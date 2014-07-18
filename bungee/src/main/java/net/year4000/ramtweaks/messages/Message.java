package net.year4000.ramtweaks.messages;

import com.ewized.utilities.bungee.util.BungeeLocale;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Message extends BungeeLocale {
    public Message(CommandSender sender) {
        super(sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null);
        localeManager = MessageManager.get();
    }
}
