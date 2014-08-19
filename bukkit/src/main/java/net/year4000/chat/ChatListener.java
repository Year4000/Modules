package net.year4000.chat;

import com.google.common.base.Ascii;
import net.year4000.chat.events.MessageReceiveEvent;
import net.year4000.chat.events.MessageSentEvent;
import net.year4000.chat.message.ChatMessage;
import net.year4000.chat.message.Message;
import net.year4000.chat.message.PlayerActor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ChatListener implements Listener, PluginMessageListener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        ChatMessage message = new ChatMessage();

        message.setFormat(Settings.get().getChatFormat());
        message.setActor(new PlayerActor(event.getPlayer()));
        message.setMessage(event.getMessage());
        message.setServer(Bukkit.getServerName());

        new MessageSentEvent(message).call();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChatSent(MessageSentEvent event) {
        // todo when we send a message
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatReceive(MessageReceiveEvent event) {
        Message message = event.getMessage();
        String msg = String.format(
            "%s%s: %s",
            Bukkit.getServerName().equalsIgnoreCase(message.getServer()) ? "" : message.getServer() + " -> ",
            message.getActor().getName(),
            message.getMessage()
        );
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        // todo check channel and create message recive event and create a new MessageReceiver
    }
}
