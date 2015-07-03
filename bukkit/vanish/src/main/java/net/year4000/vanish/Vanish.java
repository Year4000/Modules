/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.vanish;

import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.WeakHashMap;

@ModuleInfo(
    name = "Vanish",
    version = "1.0",
    description = "Hide your self from others.",
    authors = {"Year4000"}
)
@ModuleListeners({Vanish.HiddenListener.class})
public class Vanish extends BukkitModule {
    public static Map<Player, String> hidden = new WeakHashMap<>();

    @Override
    public void enable() {
        registerCommand(VanishCommands.class);
    }

    public static void updateHidden() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Player checkPlayer : Bukkit.getOnlinePlayers()) {
                if (hidden.containsKey(checkPlayer)) {
                    player.hidePlayer(checkPlayer);
                }
                else {
                    player.showPlayer(checkPlayer);
                }
            }
        }
    }

    public static class HiddenListener implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            updateHidden();
        }
    }
}
