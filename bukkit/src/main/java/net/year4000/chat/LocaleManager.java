package net.year4000.chat;

import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.URLLocaleManager;

public class LocaleManager extends URLLocaleManager {
    private static QuickCache<LocaleManager> inst = QuickCache.builder(LocaleManager.class).build();
    private static String url = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/chat/locales/";

    public LocaleManager() {
        super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static LocaleManager get() {
        return inst.get();
    }
}
