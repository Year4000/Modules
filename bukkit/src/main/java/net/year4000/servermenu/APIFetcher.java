package net.year4000.servermenu;

import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.servermenu.menus.MenuManager;
import net.year4000.utilities.Callback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class APIFetcher implements Runnable {
    private AtomicLong lastRun = new AtomicLong(System.currentTimeMillis());
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
        Throwable throwable = null;

        try {
            long time = System.currentTimeMillis() - lastRun.getAndSet(System.currentTimeMillis());
            TimeUnit.MILLISECONDS.sleep(time < 1000 ? 720L : 250L);

            manager.pullAPIData();
        } catch (Throwable t) {
            throwable = t;
        } finally {
            Throwable throwing = throwable;
            SchedulerUtil.runAsync(() -> callbackData.callback(manager, throwing));
        }

        String timestamp = new SimpleDateFormat("hh:mm:ss").format(new Date(System.currentTimeMillis()));
        ServerMenu.debug("ServerMenu " + (throwable == null ? "pulled" : "erred") + " at: " + timestamp);
    }
}
