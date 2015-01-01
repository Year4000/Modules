package net.year4000.servermenu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BungeeSender implements Listener {
    @Getter
    @Setter
    private static String currentServer = null;
    private String server;

    public BungeeSender(String server) {
        this.server = server;
    }

    public void send(Player player) {
        ServerMenu.getInst().getConnector().sendToPlayer(player, new String[]{"Connect", server});
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        if (currentServer != null) return;

        ServerMenu.getInst().getConnector().send(new String[]{"GetServer"}, (date, error) -> setCurrentServer(date.readUTF()));
    }
}