package net.year4000.hub;

import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ducktape.module.ModuleInfo;
import org.bukkit.*;

import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "Hub",
    version = "1.0",
    description = "Controls basic parts of the hub",
    authors = {"Year4000"}
)
@ModuleListeners({HubListener.class, WorldListener.class})
public class Hub extends BukkitModule {
    public static final int SPAWN_PROTECTION = 10;
    public static final GameMode GAME_MODE = GameMode.ADVENTURE;
    
    @Override
    public void enable() {
        World main = Bukkit.getWorlds().get(0);

        // Lock the world's state
        SchedulerUtil.repeatSync(() -> {
            main.setAutoSave(false);
            main.setDifficulty(Difficulty.NORMAL);
            main.setThundering(false);
            main.setThunderDuration(Integer.MAX_VALUE);
            main.setStorm(false);
            main.setWeatherDuration(Integer.MAX_VALUE);
        }, 1, TimeUnit.HOURS);
        SchedulerUtil.repeatSync(() -> {
            main.setTime(14800);
            Bukkit.getOnlinePlayers().forEach(p -> p.setFoodLevel(20));
        }, (long) 0.2, TimeUnit.SECONDS);
    }

    public static Location hubSpawn() {
        return Bukkit.getWorlds().get(0).getSpawnLocation().clone().add(0, 5, 0);
    }
}
