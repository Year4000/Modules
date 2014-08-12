package net.year4000.vanish.messages;

import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.URLLocaleManager;

class MessageManager extends URLLocaleManager {
    private static QuickCache<MessageManager> inst = QuickCache.builder(MessageManager.class).build();
    private static String url = "https://git.year4000.net/year4000/locales/raw/master/net/year4000/vanish/locales/";

    public MessageManager() {
        super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static MessageManager get() {
        return inst.get();
    }
}
