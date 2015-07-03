/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.localewatchdog;

import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ducktape.module.ModuleInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "LocaleWatchdog",
    version = "1.0",
    description = "Create events when the player changes their locale.",
    authors = {"Year4000"}
)
public class LocaleWatchdog extends BukkitModule {
    private static Map<Player, String> lastLocales = new WeakHashMap<>();

    @Override
    public void enable() {
        SchedulerUtil.repeatAsync(new WatchdogClock(), 1, TimeUnit.SECONDS);
    }

    private static class WatchdogClock implements Runnable {
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (lastLocales.containsKey(player)) {
                    String last = lastLocales.get(player);
                    String current = player.spigot().getLocale();
                    if (!last.equalsIgnoreCase(current)) {
                        lastLocales.put(player, current);
                        Event change = new PlayerChangeLocaleEvent(player, last, current);
                        Bukkit.getPluginManager().callEvent(change);
                    }
                }
                else {
                    lastLocales.put(player, player.spigot().getLocale());
                }
            }
        }
    }
}
