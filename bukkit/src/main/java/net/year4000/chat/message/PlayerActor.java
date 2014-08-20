package net.year4000.chat.message;

import com.google.gson.annotations.Since;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class PlayerActor implements Actor {
    /** The player's name */
    @Since(1.0)
    private String name;

    /** The display name of the player */
    @Since(1.0)
    private String display;

    /** The player's Minecraft's uuid */
    @Since(1.0)
    private String uuid;

    /** The player's locale code */
    @Since(1.0)
    private String locale;

    /** The world the player is in */
    @Since(1.0)
    private String world;

    /** Can the player use colors in the chat */
    @Since(1.0)
    private boolean useColors;

    public PlayerActor(Player player) {
        name = player.getName();
        display = player.getDisplayName();
        uuid = player.getUniqueId().toString();
        locale = player.getLocale();
        world = player.getWorld().getName();
        useColors = player.hasPermission("chat.colors");
    }
}
