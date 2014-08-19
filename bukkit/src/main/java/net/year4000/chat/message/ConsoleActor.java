package net.year4000.chat.message;

import com.google.gson.annotations.Since;
import lombok.Data;

@Data
public class ConsoleActor implements Actor {
    @Since(1.0)
    private String name = "Console";
}
