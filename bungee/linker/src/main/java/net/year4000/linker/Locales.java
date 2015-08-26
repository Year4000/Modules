package net.year4000.linker;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.utilities.bungee.BungeeLocale;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.LocaleKeys;
import net.year4000.utilities.locale.URLLocaleManager;

public enum Locales implements LocaleKeys<CommandSender> {
    LOCALE_CODE,
    LOCALE_NAME,

    HUB_NONE,
    HUB_ON,

    SERVER_CONNECT,
    SERVER_ON,
    SERVER_USE,
    SERVER_NO__NAME,

    LINKER_ADD,
    LINKER_NO__FOUND,
    LINKER_REMOVE,

    GENERATE_GENERATING,
    GENERATE_SUCCESS,
    GENERATE_ERROR,
    ;

    private static QuickCache<MessageManager> inst = QuickCache.builder(MessageManager.class).build();
    private static String url = "https://raw.githubusercontent.com/Year4000/Locales/master/linker/";

    @Override
    public Message apply(CommandSender sender) {
        return new Message(sender);
    }

    public static class Message extends BungeeLocale {
        public Message(ProxiedPlayer player) {
            super(player);
            localeManager = MessageManager.get();
        }

        public Message(CommandSender sender) {
            this(sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null);
        }
    }

    public static class MessageManager extends URLLocaleManager {
        public MessageManager() {
            super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
        }

        public static MessageManager get() {
            return inst.get();
        }
    }
}
