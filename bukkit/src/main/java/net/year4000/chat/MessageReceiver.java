package net.year4000.chat;

import lombok.Data;
import net.year4000.chat.channel.Channel;
import net.year4000.chat.events.MessageReceiveEvent;
import net.year4000.chat.formatter.FormatterManager;
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
            players.forEach(p -> p.sendMessage(event.getSend().send(p, event.getMessage(), FormatterManager.get().replaceAll(event.getMessage()))));
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
