package net.year4000.chat.message;

import com.google.gson.annotations.Since;
import lombok.Data;

@Data
public class PlayerActor {
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
