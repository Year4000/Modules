/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.announcer.messages;

import net.year4000.announcer.Settings;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.URLLocaleManager;

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
