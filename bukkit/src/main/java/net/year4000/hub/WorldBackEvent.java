package net.year4000.hub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class WorldBackEvent extends Event {
    @Getter
    private static final HandlerList handlers = new HandlerList();
    private Block block;

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
