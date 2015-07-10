/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapjson;

import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@EqualsAndHashCode
@ToString
public final class UserSession {
    private UUID player;
    private WeakReference<Player> minecraft;

    /** Create a user session for the player */
    public UserSession(Player player) {
        minecraft = new WeakReference<>(player);
        this.player = player.getUniqueId();
    }

    /** Get the player or throw error */
    public Player player() {
        if (minecraft.isEnqueued()) {
            minecraft = new WeakReference<>(Bukkit.getPlayer(player));
        }

        return checkNotNull(minecraft.get(), "minecraft.get()");
    }

    /** Send a message to the player */
    public void sendMessage(Object object) {
        player().sendMessage(MessageUtil.replaceColors(object.toString()));
    }

    // For Snippets //

    @Getter
    private Set<LocationVector> spawns = Sets.newHashSet();

    public void addSpawn(LocationVector vector) {
        spawns.add(vector);
    }
}
