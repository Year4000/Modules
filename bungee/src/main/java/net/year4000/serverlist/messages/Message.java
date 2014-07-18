package net.year4000.serverlist.messages;

import com.ewized.utilities.bungee.util.BungeeLocale;
import com.ewized.utilities.core.util.locale.LocaleUtil;
import com.google.common.base.Joiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.serverlist.ServerList;

import static com.ewized.utilities.core.util.MessageUtil.message;
import static com.google.common.base.Preconditions.checkNotNull;

public class Message extends BungeeLocale implements LocaleUtil {
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


    /** Translate to the specific locale with formatting */
    public String get(String key, Object... args) {
        checkNotNull(locale);
        checkNotNull(localeManager);

        if (!localeManager.isLocale(locale)) {
            ServerList.debug("(" + locale + ") " + key + " " + Joiner.on(", ").join(args));
            locale = DEFAULT_LOCALE;
        }

        return message(localeManager.getLocale(locale).getProperty(key, key), args);
    }
}
