/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.hubitems.items.passive;

import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.items.PassiveState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@FunItemInfo(
    name = "night.name",
    icon = Material.NETHER_STAR,
    description = "night.description",
    passive = PassiveState.ALLWAYS_ON
)
public class NightVision extends FunItem {
    private static List<Player> players = new CopyOnWriteArrayList<>();

    public NightVision() {
        SchedulerUtil.repeatAsync(new EffectsClock(), 1, TimeUnit.HOURS);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        addEffect(event.getPlayer());
        players.add(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        players.remove(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        addEffect(event.getPlayer());
        event.getPlayer().updateInventory();
    }

    private static void addEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true));
    }

    public static class EffectsClock implements Runnable {
        @Override
        public void run() {
            for (Player player : players) {
                if (!player.isOnline()) {
                    continue;
                }

                addEffect(player);
            }
        }
    }
}
