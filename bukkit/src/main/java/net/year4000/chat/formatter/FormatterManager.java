package net.year4000.chat.formatter;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.year4000.chat.message.Message;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormatterManager {
    private static FormatterManager inst;
    @Setter(AccessLevel.NONE)
    private List<Formatter> formats = new ArrayList<>();

    public static FormatterManager get() {
        if (inst == null) {
            inst = new FormatterManager();
        }

        return inst;
    }

    /** Add a formatter to the list of formats */
    public void addFormatter(Formatter format) {
        if (!formats.contains(format)) {
            formats.add(format);
        }
    }

    /** Replace all keys with the intended values */
    public String replaceAll(Message message) {
        String newMessage = message.getMessage();

        for (Formatter format : formats) {
            newMessage = newMessage.replaceAll(format.key(), format.value(message));
        }

        return newMessage;
    }
}
