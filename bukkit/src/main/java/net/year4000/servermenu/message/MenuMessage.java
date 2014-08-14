package net.year4000.servermenu.message;

import net.year4000.utilities.bukkit.BukkitLocale;

public class MenuMessage extends BukkitLocale {
    public MenuMessage(String locale) {
        super(null);
        this.locale = locale;
        localeManager = MenuMessageManager.get();
    }
}
