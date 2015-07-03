/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.chat.channel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Channel {
    private String channel;
    private Type type;
    private String permission;

    /** Get the channel */
    public String getChannel() {
        return channel.toUpperCase();
    }

    public enum Type {
        SYSTEM,
        USER
    }
}
