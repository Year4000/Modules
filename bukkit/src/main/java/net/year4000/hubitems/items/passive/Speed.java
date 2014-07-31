package net.year4000.hubitems.items.passive;

import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

@FunItemInfo(
    name = "speed.name",
    icon = Material.FEATHER,
    description = "speed.description",
    passive = true
)
public class Speed extends FunItem {
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        new EffectsClock(event.getPlayer());
    }

    class EffectsClock implements Runnable {
        BukkitTask task;
        Player player;

        public EffectsClock(Player runon) {
            player = runon;
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(DuckTape.get(), this, 1, 60 * 20L);
        }

        @Override
        public void run() {
            if (!player.isOnline()) {
                task.cancel();
                return;
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true));
        }
    }
}
