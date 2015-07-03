/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.servermenu.locales;

import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.servermenu.Settings;
import net.year4000.utilities.bukkit.BukkitLocale;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.URLLocaleManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageFactory extends URLLocaleManager {
    private static QuickCache<MessageFactory> inst = QuickCache.builder(MessageFactory.class).build();
    private static String url = Settings.get().getLocales();

    public MessageFactory() {
        super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static MessageFactory get() {
        return inst.get();
    }

    public static class Message extends BukkitLocale {
        /** Use a CommandSender to create a locale */
        public Message(CommandSender sender) {
            super(sender instanceof Player ? (Player) sender : null);
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
