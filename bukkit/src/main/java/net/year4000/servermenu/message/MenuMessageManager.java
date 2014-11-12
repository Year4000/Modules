package net.year4000.servermenu.message;

import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.URLLocaleManager;

public class MenuMessageManager extends URLLocaleManager {
    private static QuickCache<MenuMessageManager> inst = QuickCache.builder(MenuMessageManager.class).build();
    private static String url = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/hub/locales/";

    public MenuMessageManager() {
        super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static MenuMessageManager get() {
        return inst.get();
    }
}
