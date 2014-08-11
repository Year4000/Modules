package net.year4000.serverlinker.webserver;

import lombok.Data;
import net.md_5.bungee.api.config.ServerInfo;
import net.year4000.serverlinker.ServerLinker;
import net.year4000.serverlinker.Settings;
import net.year4000.utilities.Pinger;

import java.io.IOException;
import java.net.InetSocketAddress;

@Data
public class ServerStatus {
    private transient ServerInfo info;
    private String name;
    private ServerGroup group;
    private Pinger.StatusResponse status;

    public ServerStatus(ServerInfo server) {
        info = server;
        name = server.getName();
        group = new ServerGroup(Settings.get().getServer(name));
    }

    /** Ping the server and update the status */
    public void ping() {
        try {
            InetSocketAddress address = info.getAddress();
            status = new Pinger(address, Pinger.TIME_OUT).fetchData();
        } catch (IOException e) {
            ServerLinker.debug(name + " ping exception: " + e.getMessage());
            status = null;
        }
    }

    /** Is this server hidden */
    public boolean isHidden() {
        return name.startsWith(".") || getGroup().isHidden();
    }
}
