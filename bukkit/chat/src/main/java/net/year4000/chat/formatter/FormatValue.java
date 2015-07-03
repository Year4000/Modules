/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.chat.formatter;

import net.year4000.chat.Message;

public interface FormatValue {
    /** The value to return */
    public String value(Message message);
}
