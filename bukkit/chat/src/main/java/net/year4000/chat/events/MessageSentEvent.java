/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.chat.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.chat.Message;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MessageSentEvent extends ChatEvent {
    private Message message;
}
