package net.year4000.servermenu.locales;

import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.utilities.bukkit.BukkitLocale;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.URLLocaleManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubMessageFactory extends URLLocaleManager {
    private static QuickCache<HubMessageFactory> inst = QuickCache.builder(HubMessageFactory.class).build();
    private static String url = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/hub/locales/";

    public HubMessageFactory() {
        super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static HubMessageFactory get() {
        return inst.get();
    }

    public static class Message extends BukkitLocale {
        /** Use a CommandSender to create a locale */
        public Message(CommandSender sender) {
            super(sender instanceof Player ? (Player) sender : null);
            localeManager = HubMessageFactory.get();
        }

        /** Allow using raw locale codes */
        public Message(String code) {
            super(null);
            locale = code;
            localeManager = HubMessageFactory.get();
        }
    }
}
