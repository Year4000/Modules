package net.year4000.serverlinker.webserver;

import com.ewized.utilities.core.util.Pinger;
import lombok.Data;
import net.md_5.bungee.api.config.ServerInfo;
import net.year4000.serverlinker.Settings;

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
    public void ping() throws IOException {
        InetSocketAddress address = info.getAddress();
        status = new Pinger(address, Pinger.TIME_OUT).fetchData();
        // use bellow if we want to hide things
        /*info.ping((ping, throwable) ->  {
            try {
                status = new ServerPing(ping.getVersion(), ping.getPlayers(), ping.getDescription(), ping.getFaviconObject());
            } catch (Exception e) {
                ServerLinker.debug(e, true);
            }
        });*/
    }
}
