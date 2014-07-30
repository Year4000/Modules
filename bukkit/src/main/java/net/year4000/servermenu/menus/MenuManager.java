package net.year4000.servermenu.menus;

import lombok.Data;
import net.year4000.servermenu.ServerMenu;
import net.year4000.servermenu.Settings;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class MenuManager {
    private static MenuManager inst;
    private Map<String, String> itemMenu = new ConcurrentHashMap<>();
    private Map<String, InvMenu> menus = new ConcurrentHashMap<>();
    private Collection<ServerJson> servers;
    private Collection<ServerJson.Group> groups;

    public MenuManager() {
        pullAPIData();

        Settings.get().getMenus().forEach(menu -> {
            List<String> groups = menu.getGroups();
            if (groups.get(0) != null && itemMenu.get(menu.getTitle()) == null) {
                String firstMenu = null;

                for (String group : groups) {
                    InvMenu inv = new InvMenu(menu.isPlayers(), menu.isMotd(), group, groups.toArray(new String[groups.size()]));

                    if (firstMenu == null) {
                        firstMenu = inv.getMenuDisplay();
                    }

                    menus.put(inv.getMenuDisplay(), inv);
                }

                itemMenu.put(menu.getTitle(), firstMenu);
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
    public void openMenu(Player player, String title) {
        if (isMenu(title)) {
            player.openInventory(menus.get(itemMenu.get(title)).menu(player.getLocale()));
        }
        else if (isRawMenu(title)) {
            player.openInventory(menus.get(title).menu(player.getLocale()));
        }
        else {
            ServerMenu.debug(title + " is not a menu item.");
        }
    }

    public boolean isMenu(String title) {
        return itemMenu.get(title) != null;
    }

    public boolean isRawMenu(String title) {
        return menus.get(title) != null;
    }
}
