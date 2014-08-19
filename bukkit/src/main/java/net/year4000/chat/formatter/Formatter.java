package net.year4000.chat.formatter;

import lombok.AllArgsConstructor;
import net.year4000.chat.message.Message;
import net.year4000.chat.message.PlayerActor;

@AllArgsConstructor
public abstract class Formatter {
    private String key;

    /** The key in its key form */
    public String key() {
        return String.format("{%s}", key);
    }

    public abstract String value(Message player);
}
