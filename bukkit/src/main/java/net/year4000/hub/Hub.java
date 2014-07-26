package net.year4000.hub;

import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.core.module.ModuleInfo;
import org.bukkit.*;

@ModuleInfo(
    name = "Hub",
    version = "1.0",
    description = "Controls basic parts of the hub",
    authors = {"Year4000"}
)
@ModuleListeners({HubListener.class, WorldListener.class, WorldBack.class})
public class Hub extends BukkitModule {
    WorldBack worldBack;
    public static final GameMode GAME_MODE = GameMode.ADVENTURE;
    
    @Override
    public void enable() {
        World main = Bukkit.getWorlds().get(0);

        // Lock the world's state
        Bukkit.getScheduler().runTaskTimer(DuckTape.get(), () -> {
            main.setAutoSave(false);
            main.setDifficulty(Difficulty.PEACEFUL);
            main.setThundering(false);
            main.setThunderDuration(Integer.MAX_VALUE);
            main.setStorm(false);
            main.setWeatherDuration(Integer.MAX_VALUE);
        }, 1, Integer.MAX_VALUE / 2);
        Bukkit.getScheduler().runTaskTimer(DuckTape.get(), () -> main.setTime(14800), 1, 2);
    }

    @Override
    public void disable() {
        worldBack.cancel();
    }

    public static Location hubSpawn() {
        return Bukkit.getWorlds().get(0).getSpawnLocation().clone().add(0, 5, 0);
    }
}
