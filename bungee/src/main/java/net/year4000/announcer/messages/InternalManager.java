package net.year4000.announcer.messages;

import com.ewized.utilities.core.util.cache.QuickCache;
import com.ewized.utilities.core.util.locale.URLLocaleManager;
import net.year4000.announcer.Settings;
import net.year4000.ducktape.bungee.DuckTape;

public class InternalManager extends URLLocaleManager {
    private static String url = Settings.get().getInternalURL();
    private static QuickCache<InternalManager> inst = QuickCache.builder(InternalManager.class).build();

    public InternalManager() {
        super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static InternalManager get() {
        return inst.get();
    }
}
