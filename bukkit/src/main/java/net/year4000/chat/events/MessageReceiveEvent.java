package net.year4000.chat.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.chat.message.Message;
import org.bukkit.event.Cancellable;

@Data
@EqualsAndHashCode(callSuper = false)
public class MessageReceiveEvent extends ChatEvent implements Cancellable {
    private boolean cancelled = false;
    private MessageSend send = (player, data, msg) -> msg;
    private Message message;

    /** Construct the MessageReceiveEvent with the message */
    public MessageReceiveEvent(Message message) {
        this.message = message;
    }
}
