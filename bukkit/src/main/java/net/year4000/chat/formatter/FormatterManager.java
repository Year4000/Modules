package net.year4000.chat.formatter;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.year4000.chat.Chat;
import net.year4000.chat.Message;
import net.year4000.utilities.bukkit.MessageUtil;

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
    public boolean addFormatter(String key, FormatValue value) {
        boolean success;
        Formatter format = new Formatter(key) {
            @Override
            public String value(Message player) {
                return value.value(player);
            }
        };

        if (success = !formats.contains(format)) {
            formats.add(format);
        }

        return  success;
    }

    /** Replace all keys with the intended values */
    public String replaceAll(Message message) {
        String msg;

        // do it this way a ternary is too long
        if (message.isMeta(Chat.CHAT_FORMAT)) {
            msg = MessageUtil.replaceColors(message.getMeta(Chat.CHAT_FORMAT));
        }
        else {
            msg = MessageUtil.replaceColors(message.getMessage());
        }

        // Replace vars with the results
        for (Formatter format : formats) {
            msg = msg.replaceAll(format.key(), format.value(message));
        }

        return msg;
    }
}
