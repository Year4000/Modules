package net.year4000.servermenu;

import lombok.AllArgsConstructor;
import net.year4000.ducktape.bukkit.DuckTape;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

@AllArgsConstructor
public class BungeeSender {
    private String server;

    public BungeeSender() {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(DuckTape.get(), "BungeeCord");
    }

    public void send(Player player) {
        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos)
        ) {
            dos.writeUTF("Connect");
            dos.writeUTF(server);
            player.sendPluginMessage(DuckTape.get(), "BungeeCord", baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}