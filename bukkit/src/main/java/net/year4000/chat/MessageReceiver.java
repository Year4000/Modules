package net.year4000.chat;

import lombok.Data;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.year4000.chat.channel.Channel;
import net.year4000.chat.events.MessageReceiveEvent;
import net.year4000.chat.formatter.FormatterManager;
import net.year4000.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class MessageReceiver {
    private MessageReceiveEvent event;
    private Message message;

    public MessageReceiver(Message message) {
        this.message = message;
    }

    /**
     * This will send the send the event but also contains the event also.
     * This allows for you to create a custom event and resend it.
     */
    private void sendEventAndReassign() {
        event = new MessageReceiveEvent(message);
        event.call(); // this is where the event could change
        message = event.getMessage();
    }

    /** Send the formatted message to all players that have the channel */
    public void send() {
        sendEventAndReassign();

        if (!event.isCancelled()) {
            Set<Player> players = playersInChannel(event.getMessage().getChannel());
            players.forEach(p -> sendRawMessage(event, p));
        }
    }

    private void sendRawMessage(MessageReceiveEvent event, Player p) {
        if (wasTranslatedFor(p, event)) {
            String message = event.getSend().send(p, event.getMessage(), FormatterManager.get().replaceAll(event.getMessage()));
            char color = message.charAt(0);
            String[] split = message.split(": " + color + "f", 2);
            String json = "{text:\"" + split[0] + ": \",extra:[{text:\"" + split[1] + "\",italic:true,hoverEvent:{action:show_text,value:{text:\"" + MessageUtil.replaceColors(new LocaleManager.Msg(p).get("translator.original")) + " " + event.getMessage().getMessage() + "\"}}}]}";
            PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(json));
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
        else {
            p.sendMessage(event.getSend().send(p, event.getMessage(), FormatterManager.get().replaceAll(event.getMessage())));
        }
    }

    private boolean wasTranslatedFor(Player p, MessageReceiveEvent event) {
        return !event.getSend().send(p, event.getMessage(), FormatterManager.get().replaceAll(event.getMessage())).contains(event.getMessage().getMessage());
    }

    /** Return a set of all the players that have the channel */
    private Set<Player> playersInChannel(Channel channel) {
        return Bukkit.getOnlinePlayers().stream()
            .map(UserActor::get)
            .filter(a -> a.getReceivingChannels().contains(channel))
            .map(UserActor::getPlayer)
            .collect(Collectors.toSet());
    }
}
