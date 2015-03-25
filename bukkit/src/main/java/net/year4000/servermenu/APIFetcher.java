package net.year4000.servermenu;

import net.year4000.servermenu.menus.MenuManager;
import net.year4000.utilities.Callback;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.Date;

public class APIFetcher implements Runnable {
    private final MenuManager manager = MenuManager.get();
    private final Callback<MenuManager> callbackData = (data, error) -> {
        if (error == null) {
            data.getMenus().values().parallelStream().forEach(menu -> {
                if (menu.needNewInventory()) {
                    menu.regenerateMenuViews();
                }
                else {
                    menu.updateServers();
                }
            });
        }
        else {
            ServerMenu.log(new Exception(error), true);
        }
    };

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().size() == 0) return;

        Throwable throwable = null;

        try {
            manager.pullAPIData();
        } catch (Throwable t) {
            throwable = t;
        } finally {
            callbackData.callback(manager, throwable);
        }

        String timestamp = new SimpleDateFormat("hh:mm:ss").format(new Date(System.currentTimeMillis()));
        ServerMenu.debug("ServerMenu " + (throwable == null ? "pulled" : "erred") + " at: " + timestamp);
    }
}
