/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.chat;

import lombok.Data;
import net.year4000.chat.channel.Channel;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@Data
public class UserActor {
    /** Know Actors */
    private static Map<Player, UserActor> playerActors = new WeakHashMap<>();

    private UserActor(Player player) {
        this.player = player;
    }

    /** Get or create an instance of ItemActor */
    public static UserActor get(Player player) {
        if (!playerActors.containsKey(player)) {
            UserActor actor = new UserActor(player);
            playerActors.put(player, actor);
            return actor;
        }

        return playerActors.get(player);
    }

    /** The player this UserActor is for */
    private Player player;

    /** The messages from the channels that the player will receive */
    private Set<Channel> receivingChannels = new HashSet<Channel>() {{
        add(new Channel("CHAT", Channel.Type.USER, ""));
    }};

    /** The channel that you are sending message to */
    private Channel sendingChannel = new Channel("CHAT", Channel.Type.USER, "");


}
