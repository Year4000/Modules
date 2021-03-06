/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class ChatEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /** Call the event */
    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }
}
