package net.year4000.chat.events;

import net.year4000.chat.message.Message;
import org.bukkit.entity.Player;

public interface MessageSend {
    public String send(Player player, Message message, String msg);
}
