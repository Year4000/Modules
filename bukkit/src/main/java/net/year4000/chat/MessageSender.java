package net.year4000.chat;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.year4000.ducktape.bukkit.DuckTape;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class MessageSender {
    private static MessageSender inst;

    private MessageSender() {
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin) DuckTape.get(), Chat.PLUGIN_CHANNEL);
    }

    public static MessageSender get() {
        if (inst == null) {
            inst = new MessageSender();
        }

        return inst;
    }

    public void send(Message message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(Chat.get().getModuleInfo().name());
        out.writeUTF(Chat.GSON.toJson(message));

        Bukkit.getOnlinePlayers()[0].sendPluginMessage((Plugin) DuckTape.get(), Chat.PLUGIN_CHANNEL, out.toByteArray());
    }
}
