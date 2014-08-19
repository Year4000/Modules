package net.year4000.chat.message;

import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoginMessage extends BaseMessage {
    /** The player actor that has login */
    @Since(1.0)
    private PlayerActor player;
}
