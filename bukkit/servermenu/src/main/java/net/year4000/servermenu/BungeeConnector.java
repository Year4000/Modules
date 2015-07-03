/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.servermenu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.year4000.utilities.bukkit.MessagingChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class BungeeConnector implements Listener {
    @Getter
    @Setter
    private static String currentServer = null;
    private static MessagingChannel connector = MessagingChannel.get();
    private String server;

    /** Send the player to the given server */
    public void send(Player player) {
        String[] data = new String[]{"Connect", server};
        connector.sendToPlayer(player, data);
    }

    /** When a player joins get the name of the current server */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (currentServer != null) return;

        String[] data = new String[]{"GetServer"};
        connector.send(data, (server, error) -> setCurrentServer(server.readUTF()));
    }
}
