package net.year4000.localewatchdog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class PlayerChangeLocaleEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private String from;
    private String to;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
