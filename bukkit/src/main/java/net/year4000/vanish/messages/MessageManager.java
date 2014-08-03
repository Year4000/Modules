package net.year4000.vanish.messages;

import com.ewized.utilities.core.util.cache.QuickCache;
import com.ewized.utilities.core.util.locale.URLLocaleManager;
import net.year4000.ducktape.bukkit.DuckTape;

public class MessageManager extends URLLocaleManager {
    private static QuickCache<MessageManager> inst = QuickCache.builder(MessageManager.class).build();
    private static String url = "https://git.year4000.net/year4000/locales/raw/master/net/year4000/vanish/locales/";

    public MessageManager() {
        super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static MessageManager get() {
        return inst.get();
    }
}
