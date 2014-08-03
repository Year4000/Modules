package net.year4000.hubitems.items.passive;

import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

@FunItemInfo(
    name = "night.name",
    icon = Material.NETHER_STAR,
    description = "night.description",
    passive = true
)
public class NightVision extends FunItem {
    private BukkitTask task;

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        task = new EffectsClock(event.getPlayer()).task;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        task.cancel();
        task = new EffectsClock(event.getPlayer()).task;
    }

    class EffectsClock implements Runnable {
        BukkitTask task;
        Player player;

        public EffectsClock(Player runon) {
            player = runon;
            task = SchedulerUtil.repeatSync(this, 1, TimeUnit.HOURS);;
        }

        @Override
        public void run() {
            if (!player.isOnline()) {
                task.cancel();
                return;
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true));
        }
    }
}
