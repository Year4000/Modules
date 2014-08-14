package net.year4000.servermenu.menus;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.servermenu.ServerMenu;
import net.year4000.servermenu.Settings;
import net.year4000.servermenu.message.MenuMessage;
import net.year4000.servermenu.message.MenuMessageManager;
import net.year4000.utilities.bukkit.BukkitLocale;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Data
public class MenuManager {
    /** Menu Types */
    public static enum Type {
        RAW_MENU,
        NORMAL
    }

    private static MenuManager inst;
    private List<LocaleMenu> itemMenus = new CopyOnWriteArrayList<>();
    private Map<String, InvMenu> menus = new ConcurrentHashMap<>();
    private Collection<ServerJson> servers;
    private Collection<ServerJson.Group> groups;

    public MenuManager() {
        pullAPIData();
        updateServers();
    }

    public void updateServers() {
        menus.clear();
        itemMenus.clear();

        Settings.get().getMenus().forEach(menu -> {
            List<String> groups = menu.getGroups();
            if (groups.get(0) != null) {
                String firstMenu = null;

                for (String group : groups) {
                    InvMenu inv = new InvMenu(this, menu.isPlayers(), menu.isMotd(), group, groups.toArray(new String[groups.size()]));

                    if (firstMenu == null) {
                        firstMenu = inv.getMenuDisplay();
                    }

                    menus.put(inv.getMenuDisplay(), inv);
                }

                // add all the locale values to the map
                for (Locale locale : MenuMessageManager.get().getLocales().keySet()) {
                    String menuTitle = MessageUtil.stripColors(new MenuMessage(locale.toString()).get(menu.getTitle()));
                    //ServerMenu.debug("FirstMenu: " + firstMenu);
                    //ServerMenu.debug("Registering server menu " + locale.toString() + " |" + menuTitle + "| " + menuTitle.hashCode());
                    itemMenus.add(new LocaleMenu(locale, menuTitle, firstMenu));
                }
            }
            else {
                ServerMenu.debug(menu.getTitle() + " does not have any groups!");
            }
        });
    }

    public static MenuManager get() {
        if (inst == null) {
            inst = new MenuManager();
        }

        return inst;
    }

    /** Pull the servers from the api server */
    public void pullAPIData() {
        servers = APIManager.getServers();
        groups = APIManager.getGroups(servers);
    }

    /** Open the menu */
    public void openMenu(Type type, Player player, String title) {
        Locale locale = new Locale(MenuMessageManager.get().isLocale(player.getLocale()) ? player.getLocale() : BukkitLocale.DEFAULT_LOCALE);
        //ServerMenu.debug(locale.toString());

        if (type == Type.NORMAL) {
            String menu = itemMenus.stream()
                .filter(m -> m.getLocale().equals(locale) && m.getConfigReplacedValue().equals(title))
                .map(LocaleMenu::getMenu)
                .collect(Collectors.toList()).get(0);
            //ServerMenu.debug(player.getLocale());
            player.openInventory(menus.get(menu).openMenu(locale.toString()));
        }
        else if (type == Type.RAW_MENU) {
            player.openInventory(menus.get(title).openMenu(locale.toString()));
        }
        else {
            throw new UnsupportedOperationException("|" + title + "| (" + title.hashCode() + ") is not a menu item.");
        }
    }

    public boolean isMenu(Player player, String title) {
        Locale locale = new Locale(MenuMessageManager.get().isLocale(player.getLocale()) ? player.getLocale() : BukkitLocale.DEFAULT_LOCALE);
        int size = itemMenus.stream()
            .filter(m -> m.getLocale().equals(locale) && m.getConfigReplacedValue().equals(title))
            .map(LocaleMenu::getMenu)
            .collect(Collectors.toList()).size();

        //ServerMenu.debug("ServerMenu Item: " + size);

        return size > 0;
    }

    @Data
    @AllArgsConstructor
    private class LocaleMenu {
        private Locale locale;
        private String configReplacedValue;
        private String menu;
    }
}
