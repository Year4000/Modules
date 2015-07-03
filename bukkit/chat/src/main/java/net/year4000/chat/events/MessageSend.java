/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.chat.events;

import net.year4000.chat.Message;
import org.bukkit.entity.Player;

public interface MessageSend {
    public String send(Player player, Message message, String msg);
}
