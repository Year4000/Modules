package net.year4000.modules;

import com.ewized.utilities.bungee.BungeePlugin;
import net.year4000.announcer.Announcer;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.ducktape.core.loader.ClassModuleLoader;
import net.year4000.infractions.Infractions;
import net.year4000.ramtweaks.RamTweaks;
import net.year4000.serverlinker.ServerLinker;
import net.year4000.serverlist.ServerList;

import java.util.Set;

public class Modules extends BungeePlugin {
    private DuckTape duckTape = DuckTape.get();
    private Set<Class<?>> clazz;

    @Override
    public void onLoad() {
        clazz = new ClassModuleLoader(duckTape.getModules())
            .add(Announcer.class)
            .add(ServerList.class)
            .add(RamTweaks.class)
            .add(Infractions.class)
            .add(ServerLinker.class)
            .getClasses();

        for (Class<?> cl : clazz) {
            duckTape.getModules().loadModule(cl);
        }
    }
}
