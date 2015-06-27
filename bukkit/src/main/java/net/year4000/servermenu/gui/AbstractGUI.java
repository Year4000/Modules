package net.year4000.servermenu.gui;

import net.year4000.servermenu.InventoryGUI;
import net.year4000.servermenu.ServerMenu;
import net.year4000.servermenu.locales.MessageFactory;
import net.year4000.servermenu.views.CloseView;
import net.year4000.servermenu.views.IconView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class AbstractGUI implements Runnable {
    /** The locales for the menus */
    protected final Map<Locale, InventoryGUI> menus = new ConcurrentHashMap<>();
    /** The last state of the generate method */
    protected Map<Locale, IconView[][]> last = new ConcurrentHashMap<>();

    /** Populates the menus with known locales */
    public void populateMenu(Function<Locale, String> function, int rows) {
        Collection<Locale> locales = ServerMenu.inst.getLocales();

        for (Locale locale : locales) {
            String title = function.apply(locale);
            InventoryGUI inventoryGUI = new InventoryGUI(title, rows);
            menus.put(locale, inventoryGUI);
        }
    }

    /** Open the inventory that matches the player locale */
    public void openInventory(Player player) {
        Locale locale = new Locale(player.spigot().getLocale());
        Locale english = new Locale(MessageFactory.DEFAULT_LOCALE);
        InventoryGUI gui = menus.getOrDefault(locale, menus.get(english));
        player.openInventory(gui.getInventory());
    }

    /** Process the action for the given IconView */
    public void processAction(Player player, int row, int col) {
        try {
            Locale locale = new Locale(player.spigot().getLocale());
            locale = last.containsKey(locale) ? locale : Locale.US;
            IconView[][] views = last.get(locale);
            IconView view = views[row][col];
            view.action(locale, player, menus.get(locale));
        }
        catch (Exception e) {
            ServerMenu.debug("AbstractGUI processAction(): ");
            ServerMenu.debug(e, true);
        }
    }

    /** Get the inventory for the specific locale or english by default */
    public Inventory getInventory(Locale locale) {
        locale = menus.containsKey(locale) ? locale : Locale.US;
        return menus.get(locale).getInventory();
    }

    /** Handle the preProcess of the menu */
    public abstract void preProcess() throws Exception;

    /** Generate the 2d array for the menu */
    public abstract IconView[][] generate(Locale locale);

    /** Handle the pre and post processing of the menu gui */
    @Override
    public void run() {
        // Run preProcess
        try {
            preProcess();
        }
        catch (Exception e) {
            ServerMenu.debug(e, false);
        }

        // Store the IconView in the inventory
        menus.forEach((l, i) -> {
            IconView[][] view;

            // Run the generate
            try {
                view = generate(l);
            }
            catch (Exception e) {
                ServerMenu.debug(e, false);
                view = new IconView[][]{{null, null, null, null, new CloseView(l)}};
            }

            last.put(l, view);
            i.setIcons(view);
            i.populate();
        });
    }
}
