package net.year4000.vote;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.utilities.bungee.BungeeLocale;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.URLLocaleManager;

import java.util.Locale;

public enum VoteMessage{
    Y4K_VOTE_CHAT,
    Y4K_VOTE_CLICK,
    ;

    /** Translate the Locales enum to locale in the players language */
    public String translate(CommandSender player, Object... args) {
        String code = this.name().toLowerCase().replaceAll("__", "-").replaceAll("_", ".");
        return new MessageFactory.Message(player).get(code, args);
    }

    /** Translate the Locales enum to locale in the players language */
    public String translate(String locale, Object... args) {
        String code = this.name().toLowerCase().replaceAll("__", "-").replaceAll("_", ".");
        return new MessageFactory.Message(locale).get(code, args);
    }

    /** Translate the Locales enum to locale in the players language */
    public String translate(Locale locale, Object... args) {
        return translate(locale.toString(), args);
    }

    static class MessageFactory extends URLLocaleManager {
        private static QuickCache<MessageFactory> inst = QuickCache.builder(MessageFactory.class).build();
        private static String url = "https://raw.githubusercontent.com/Year4000/Locales/master/year4000/";

        public MessageFactory() {
            super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
        }

        public static MessageFactory get() {
            return inst.get();
        }

        public static class Message extends BungeeLocale {
            /** Use a CommandSender to create a locale */
            public Message(CommandSender sender) {
                super(sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null);
                localeManager = MessageFactory.get();
            }

            /** Allow using raw locale codes */
            public Message(String code) {
                super(null);
                locale = code;
                localeManager = MessageFactory.get();
            }
        }
    }
}
