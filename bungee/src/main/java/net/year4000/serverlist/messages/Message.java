package net.year4000.serverlist.messages;

import com.ewized.utilities.bungee.util.BungeeLocale;
import com.ewized.utilities.core.util.locale.LocaleUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Message extends BungeeLocale {
    public Message(ProxiedPlayer player) {
        super(player);
        this.localeManager = MessageManager.get();
    }

    public Message(String locale) {
        super(null);
        this.localeManager = MessageManager.get();
        this.locale = locale == null ? DEFAULT_LOCALE : locale;
    }

    public Message(CommandSender sender) {
        this(sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null);
    }
}
