package net.year4000.serverlist.messages;

import com.ewized.utilities.core.util.locale.LocaleManager;
import net.year4000.serverlist.ServerList;
import net.year4000.serverlist.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MessageManager extends LocaleManager {
    private static MessageManager inst;

    private MessageManager() {
        super(ServerList.class);
    }

    public static MessageManager get() {
        if (inst == null) {
            inst = new MessageManager();
        }

        return inst;
    }

    @Override
    protected void loadLocales(String path) {
        for (String locale : new Settings().getLocales()) {
            try {
                File file = new File(ServerList.getInst().getDataFolder() + File.separator + "locales", locale + ".properties");
                loadLocale(locale, new FileInputStream(file));
            } catch (FileNotFoundException e) {
                ServerList.debug(e, true);
            }
        }
    }

    /** Reload locales */
    public void reload() {
        inst = new MessageManager();
    }
}
