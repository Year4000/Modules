package net.year4000.chat.message;

import net.year4000.chat.channel.Channel;

public interface Message {
    /** The Actor that this message is from */
    public Actor getActor();

    /** The server that the message originated from */
    public String getServer();

    /** The message of this message the finished product */
    public String getMessage();

    /** The raw message of this message the actual text of the message */
    //public String getRawMessage();

    /** The channel that the message is sent to */
    public Channel getChannel();
}
