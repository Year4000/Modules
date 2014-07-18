package net.year4000.ramtweaks.messages;

import com.ewized.utilities.core.util.locale.LocaleManager;
import net.year4000.ramtweaks.RamTweaks;

public class MessageManager extends LocaleManager {
    private static MessageManager inst;
    protected static final String LOCALE_PATH = "/net/year4000/ramtweaks/locales/";
    private static String[] localeCodes = {"en_US", "pt_BR"};

    private MessageManager() {
        super(RamTweaks.class);
    }

    public static MessageManager get() {
        if (inst == null) {
            inst = new MessageManager();
        }

        return inst;
    }

    @Override
    protected void loadLocales(String path) {
        for (String locale : localeCodes) {
            loadLocale(locale, clazz.getResourceAsStream(LOCALE_PATH + locale + ".properties"));
        }
    }

    /** Reload locales */
    public void reload() {
        inst = new MessageManager();
    }
}
