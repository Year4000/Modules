package net.year4000.chat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.year4000.chat.events.MessageReceiveEvent;
import net.year4000.chat.events.MessageSentEvent;
import net.year4000.ducktape.bukkit.DuckTape;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ChatListener implements Listener, PluginMessageListener {
    public ChatListener() {
        Bukkit.getMessenger().registerIncomingPluginChannel((Plugin) DuckTape.get(), Chat.PLUGIN_CHANNEL, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Message message = new Message();

        message.setMeta(Chat.CHAT_FORMAT, Settings.get().getChatFormat());
        message.setMeta(Chat.PLAYER_COLORS, Chat.isVIP(player) ? "true" : "false");
        message.setMeta(Chat.PLAYER_LOCALE, player.spigot().getLocale());
        message.setMeta(Chat.PLAYER_DISPLAY, player.getDisplayName());
        message.setActorName(player.getName());
        message.setActorUUID(player.getUniqueId());
        message.setMessage(event.getMessage());
        message.setServer(Bukkit.getServerName());
        message.setChannel(UserActor.get(player).getSendingChannel());

        new MessageSentEvent(message).call();

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChatSent(MessageSentEvent event) {
        // todo send through BungeeCord
        // MessageSender.get().send(event.getMessage());
        // send to server self
        new MessageReceiver(event.getMessage()).send();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatReceive(MessageReceiveEvent event) {
        Message message = event.getMessage();
        String msg = String.format(
            "%s%s: %s",
            Bukkit.getServerName().equalsIgnoreCase(message.getServer()) ? "" : message.getServer() + " -> ",
            message.getActorName(),
            message.getMessage()
        );
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equals(Chat.PLUGIN_CHANNEL)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

        // Is message for this plugin
        if (!in.readUTF().equals(Chat.get().getModuleInfo().name())) return;

        Message message = Chat.GSON.fromJson(in.readUTF(), Message.class);

        // Check if the message is in sync before receiving it.
        if (message.getTime() + 100 > System.currentTimeMillis()) {
            new MessageReceiver(message).send();
        }
    }
}
