package net.year4000.chat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.year4000.chat.events.MessageReceiveEvent;
import net.year4000.chat.events.MessageSentEvent;
import net.year4000.chat.message.ChatMessage;
import net.year4000.chat.message.Message;
import net.year4000.chat.message.PlayerActor;
import net.year4000.ducktape.bukkit.DuckTape;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ChatListener implements Listener, PluginMessageListener {
    public ChatListener() {
        Bukkit.getMessenger().registerIncomingPluginChannel(DuckTape.get(), "Chat", this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChatMessage message = new ChatMessage();

        message.setFormat(Settings.get().getChatFormat());
        message.setActor(new PlayerActor(player));
        message.setMessage(event.getMessage());
        message.setServer(Bukkit.getServerName());
        message.setChannel(UserActor.get(player).getSendingChannel());

        new MessageSentEvent(message).call();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChatSent(MessageSentEvent event) {
        MessageSender.get().send(event.getMessage());
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
        if (!channel.equals("Chat")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

        Message message = Chat.GSON.fromJson(in.readUTF(), Message.class);

        new MessageReceiver(message).send();
    }
}
