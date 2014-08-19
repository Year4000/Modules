package net.year4000.chat.message;

import com.google.gson.annotations.Since;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class ChatMessage {
    /** The player that send the message */
    @Since(1.0)
    private Player player;

    /** The message that this
    @Since(1.0)
    private String message;

    @Since(1.0)
    private String format;

    @Data
    public class Player {
        @Since(1.0)
        private String name;

        @Since(1.0)
        private String uuid;

        @Since(1.0)
        private String locale;

        @Since(1.0)
        private String world;

        @Since(1.0)
        private String server;
    }
}
