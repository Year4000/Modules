/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.chat;

import com.google.gson.annotations.Since;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.year4000.chat.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Message {
    private static final String EMPTY = "empty";

    /** The time the message was sent in millis */
    @Since(1.0)
    @Setter(AccessLevel.NONE)
    private long time = System.currentTimeMillis();

    /** The actor's NAME that sent this message */
    @Since(1.0)
    private String actorName;

    /** The actor's UUID that sent this message */
    @Since(1.0)
    private UUID actorUUID;

    /** The server that this message originated from */
    @Since(1.0)
    private String server;

    /** The message */
    @Since(1.0)
    private String message;

    /** The channel the message is for */
    @Since(1.0)
    private Channel channel;

    /** Meta data that should be sent with the message */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Since(1.0)
    private Map<String, String> meta = new HashMap<>();

    /** Get the meta value or return `key not found` */
    public String getMeta(String key) {
        return meta.getOrDefault(key, EMPTY);
    }

    /** Set or update the meta value */
    public String setMeta(String key, String value) {
        return meta.put(key, value);
    }

    /** Does the meta key exists */
    public boolean isMeta(String key) {
        return !getMeta(key).equals(EMPTY);
    }
}
