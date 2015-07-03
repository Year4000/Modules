/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.welcome;

import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.URLLocaleManager;

public class WelcomeMessagesManager extends URLLocaleManager {
    private static QuickCache<WelcomeMessagesManager> inst = QuickCache.builder(WelcomeMessagesManager.class).build();
    private static String url = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/hub/locales/";

    public WelcomeMessagesManager() {
        super(Welcome.getLog(), url, parseJson(url + LOCALES_JSON));
    }

    public static WelcomeMessagesManager get() {
        return inst.get();
    }
}
