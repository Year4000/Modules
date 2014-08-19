package net.year4000.chat.message;

import com.google.gson.annotations.Since;
import lombok.Data;
import net.year4000.chat.channel.Channel;
import org.bukkit.Bukkit;

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
}
