package net.year4000.modules;

import com.ewized.utilities.bukkit.BukkitPlugin;
import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.ducktape.core.loader.ClassModuleLoader;
import net.year4000.ramtweaks.RamTweaks;

import java.util.Set;

public class Modules extends BukkitPlugin {
    private DuckTape duckTape;
    private Set<Class<?>> clazz;

    @Override
    public void onLoad() {
        duckTape = DuckTape.get();
        clazz = new ClassModuleLoader(duckTape.getModules())
            .add(RamTweaks.class)
            .getClasses();

        for (Class<?> cl : clazz) {
            duckTape.getModules().loadModule(cl);
        }
    }
}
