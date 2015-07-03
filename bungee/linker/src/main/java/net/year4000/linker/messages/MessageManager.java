/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.linker.messages;

import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.URLLocaleManager;

class MessageManager extends URLLocaleManager {
    private static QuickCache<MessageManager> inst = QuickCache.builder(MessageManager.class).build();
    private static String url = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/serverlinker/locales/";

    public MessageManager() {
        super(DuckTape.get().getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static MessageManager get() {
        return inst.get();
    }
}
