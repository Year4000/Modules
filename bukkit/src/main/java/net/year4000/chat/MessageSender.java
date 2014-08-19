package net.year4000.chat;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.year4000.chat.message.Message;
import net.year4000.ducktape.bukkit.DuckTape;
import org.bukkit.Bukkit;

public class MessageSender {
    private static MessageSender inst;

    private MessageSender() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(DuckTape.get(), "Chat");
    }

    public static MessageSender get() {
        if (inst == null) {
            inst = new MessageSender();
        }

        return inst;
    }

    public void send(Message message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Chat.GSON.toJson(message));

        Bukkit.getOnlinePlayers()[0].sendPluginMessage(DuckTape.get(), "Chat", out.toByteArray());
    }
}
