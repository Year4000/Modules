package net.year4000.chat.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.chat.channel.Channel;
import net.year4000.chat.message.Message;
import org.bukkit.event.Cancellable;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MessageReceiveEvent extends ChatEvent implements Cancellable {
    private boolean cancelled;
    private Message message;
}
