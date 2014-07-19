package net.year4000.serverlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class PingServer {
    private static final Map<String, String> ips = new HashMap<>();
    private static final Map<String, String> locale = new HashMap<>();
    private PendingConnection connection;
    private ServerPing response;

    /**
     * Add the player to keep track of them.
     * @param player The player to track.
     */
    public static void addPlayer(ProxiedPlayer player) {
        if (player == null) return;
        String address = cleanAddress(player.getAddress());
        ips.put(address, player.getName());
        locale.put(address, player.getLocale().toString());
    }

    /**
     * Get the player's username with the given IP
     * @param address The address to check with.
     * @return null|player username
     */
    public String getPlayer(InetSocketAddress address) {
        String ip = cleanAddress(address);
        return ips.containsKey(ip) ? ips.get(ip) : null;
    }

    /**
     * Get the player's locale with the given IP
     * @param address The address to check with.
     * @return null|player locale
     */
    public String getLocale(InetSocketAddress address) {
        String ip = cleanAddress(address);
        return locale.containsKey(ip) ? locale.get(ip) : null;
    }

    /**
     * Clean the address with out the port.
     * @param address The address to clean.
     * @return The clean address in a string.
     */
    public static String cleanAddress(InetSocketAddress address) {
        return address.getAddress().toString().split(":")[0];
    }

    @Override
    public int hashCode() {
        return cleanAddress(connection.getAddress()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PingServer && obj.hashCode() == hashCode();
    }
}
