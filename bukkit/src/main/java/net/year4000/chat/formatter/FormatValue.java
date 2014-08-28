package net.year4000.chat.formatter;

import net.year4000.chat.Message;

public interface FormatValue {
    /** The value to return */
    public String value(Message message);
}
