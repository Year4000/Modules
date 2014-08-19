package net.year4000.chat.message;

import com.google.gson.annotations.Since;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class PlayerActor implements Actor {
    @Since(1.0)
    private String name;

    @Since(1.0)
    private String display;

    @Since(1.0)
    private String uuid;

    @Since(1.0)
    private String locale;

    @Since(1.0)
    private String world;

    public PlayerActor(Player player) {
        name = player.getName();
        display = player.getDisplayName();
        uuid = player.getUniqueId().toString();
        locale = player.getLocale();
        world = player.getWorld().getName();
    }
}
