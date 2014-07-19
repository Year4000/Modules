package net.year4000.infractions;

import com.ewized.utilities.core.util.cache.QuickCache;
import com.ewized.utilities.core.util.locale.URLLocaleManager;
import net.year4000.ducktape.bungee.DuckTape;

public class MessageManager extends URLLocaleManager {
    private static QuickCache<MessageManager> inst = QuickCache.builder(MessageManager.class).build();
    private static String url = Config.get().getUrl();

    public MessageManager() {
        super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static MessageManager get() {
        return inst.get();
    }
}
