package net.year4000.serverlist.messages;

import com.ewized.utilities.core.util.cache.QuickCache;
import com.ewized.utilities.core.util.locale.URLLocaleManager;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.serverlist.Settings;

public class MessageManager extends URLLocaleManager {
    private static QuickCache<MessageManager> inst = QuickCache.builder(MessageManager.class).build();
    private static String url = new Settings().getUrl();

    public MessageManager() {
        super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static MessageManager get() {
         return inst.get();
    }
}
