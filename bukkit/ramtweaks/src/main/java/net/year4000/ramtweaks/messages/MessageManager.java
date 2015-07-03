/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.ramtweaks.messages;

import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.ramtweaks.Settings;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.URLLocaleManager;

class MessageManager extends URLLocaleManager {
    private static QuickCache<MessageManager> inst = QuickCache.builder(MessageManager.class).build();
    private static String url = Settings.get().getUrl();

    public MessageManager() {
        super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static MessageManager get() {
        return inst.get();
    }
}
