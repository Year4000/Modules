package net.year4000.servermenu;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.year4000.ducktape.bukkit.DuckTape;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

@AllArgsConstructor
public class BungeeSender implements Listener, PluginMessageListener {
    @Getter
    private static String currentServer = Bukkit.getServerName();
    private String server;

    public BungeeSender() {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(DuckTape.get(), "BungeeCord");
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(DuckTape.get(), "BungeeCord", this);
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

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (in.readUTF().equals("GetServer")) {
            currentServer = in.readUTF();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerInteractEvent event) {
        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos)
        ) {
            dos.writeUTF("GetServer");
            event.getPlayer().sendPluginMessage(DuckTape.get(), "BungeeCord", baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}