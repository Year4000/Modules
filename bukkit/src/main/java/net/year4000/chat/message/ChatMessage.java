package net.year4000.chat.message;

import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatMessage extends BaseMessage {
    /** The chat format to use */
    @Since(1.0)
    private String format;
}
