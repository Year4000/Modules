package net.year4000.colormotd;

import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

@ModuleInfo(
    name = "ColorMotd",
    version = "1.2",
    description = "Convert Minecraft color codes to real colors",
    authors = {"Year4000"}
)
@ModuleListeners({ColorMotd.ColorListener.class})
public class ColorMotd extends BukkitModule {
    public static class ColorListener implements Listener {
        @EventHandler
        public void onPing(ServerListPingEvent event) {
            event.setMotd(MessageUtil.replaceColors(event.getMotd()));
        }
    }
}
