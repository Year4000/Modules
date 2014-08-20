package net.year4000.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.year4000.chat.channel.Channel;
import net.year4000.chat.events.MessageReceiveEvent;
import net.year4000.chat.formatter.FormatterManager;
import net.year4000.chat.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class MessageReceiver {
    private MessageReceiveEvent event;
    private Message message;

    public MessageReceiver(Message message) {
        this.message = message;
    }

    private void sendEventAndReassign() {
        event = new MessageReceiveEvent(false, message);
        event.call();
        message = event.getMessage();
    }

    /** Send the formatted message to all players that have the channel */
    public void send() {
        sendEventAndReassign();

        if (!event.isCancelled()) {
            playersInChannel(message.getChannel()).forEach(p -> p.sendMessage(FormatterManager.get().replaceAll(message)));
        }
    }

    /** Return a set of all the players that have the channel */
    private Set<Player> playersInChannel(Channel channel) {
        return Arrays.asList(Bukkit.getOnlinePlayers()).stream()
            .map(UserActor::get)
            .filter(a -> a.getReceivingChannels().contains(channel))
            .map(UserActor::getPlayer)
            .collect(Collectors.toSet());
    }
}
