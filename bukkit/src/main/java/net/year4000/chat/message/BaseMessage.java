package net.year4000.chat.message;

import com.google.gson.annotations.Since;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.year4000.chat.channel.Channel;

import java.util.HashMap;
import java.util.Map;

@Data
public class BaseMessage implements Message {
    /** The actor that sent this message */
    @Since(1.0)
    private Actor actor;

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
        return meta.getOrDefault(key, "key not found");
    }

    /** Set or update the meta value */
    public String setMeta(String key, String value) {
        return meta.put(key, value);
    }
}
