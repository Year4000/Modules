package net.year4000.chat.message;

public interface Message {
    /** The Actor that this message is from */
    public Actor getActor();

    /** The server that the message originated from */
    public String getServer();

    /** Get the message of this message */
    public String getMessage();
}
