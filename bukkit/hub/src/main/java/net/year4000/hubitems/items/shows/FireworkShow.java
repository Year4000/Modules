package net.year4000.hubitems.items.shows;

import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.hubitems.items.Action;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.utils.Common;
import net.year4000.utilities.bukkit.BukkitUtil;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@FunItemInfo(
    name = "fireworkshow.name",
    icon = Material.FIREWORK,
    description = "fireworkshow.description",
    permissionLocale = "icestaff.permission",
    permission = {"mu", "pi", "sigma", "phi", "delta", "omega"},
    mana = 0.6F,
    action = Action.RIGHT
)
public class FireworkShow extends FunItem {
    private List<Color> colors = BukkitUtil.DYECOLOR_MAP.values().stream().collect(Collectors.toList());
    private Random rand = new Random();

    @EventHandler
    public void use(PlayerInteractEvent event) {
        if (!isItem(event.getAction(), event.getPlayer())) return;

        Location start = event.getPlayer().getLocation().clone();

        if (cost(event.getPlayer(), info.mana())) {
            BukkitTask task = SchedulerUtil.repeatSync(() -> {
                for (Location loc : Common.getPointsCircle(start, 8, 3.5)) {
                    Firework firework = loc.getWorld().spawn(loc, Firework.class);
                    randomEffects(firework);
                }
            }, 1, TimeUnit.SECONDS);
            SchedulerUtil.runSync(task::cancel, 5, TimeUnit.SECONDS);
        }

        event.setCancelled(true);
    }

    /** Generate a random firework */
    public void randomEffects(Firework firework) {
        FireworkEffect effect = FireworkEffect.builder()
            .flicker(rand.nextBoolean())
            .trail(rand.nextBoolean())
            .withColor(randomColor())
            .withFade(randomColor())
            .with(randomType())
            .build();
        FireworkMeta meta = firework.getFireworkMeta();
        meta.clearEffects();
        meta.addEffect(effect);
        meta.setPower(1);
        firework.setFireworkMeta(meta);
    }

    /** Generate a random color */
    public Color randomColor() {
        return colors.get(rand.nextInt(colors.size()));
    }

    /** Generate a random type */
    public FireworkEffect.Type randomType() {
        return FireworkEffect.Type.values()[rand.nextInt(FireworkEffect.Type.values().length)];
    }
}
