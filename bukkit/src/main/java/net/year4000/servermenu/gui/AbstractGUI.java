package net.year4000.servermenu.gui;

import net.year4000.servermenu.InventoryGUI;
import net.year4000.servermenu.ServerMenu;
import net.year4000.servermenu.locales.MessageFactory;
import net.year4000.servermenu.views.IconView;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractGUI implements Runnable {
    /** The locales for the menus */
    protected final Map<Locale, InventoryGUI> menus = new ConcurrentHashMap<>();

    public void openInventory(Player player) {
        Locale locale = new Locale(player.getLocale());
        Locale english = new Locale(MessageFactory.DEFAULT_LOCALE);
        InventoryGUI gui = menus.getOrDefault(locale, menus.get(english));
        player.openInventory(gui.getInventory());
    }

    /** Generate the 2d array for the menu */
    public abstract IconView[][] generate(Locale locale);
}
