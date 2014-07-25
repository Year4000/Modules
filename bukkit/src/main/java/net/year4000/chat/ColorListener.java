package net.year4000.chat;

import com.ewized.utilities.bukkit.util.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ColorListener implements Listener {
    @EventHandler
    public void onPing(ServerListPingEvent event) {
        event.setMotd(MessageUtil.replaceColors(event.getMotd()));
    }
}
