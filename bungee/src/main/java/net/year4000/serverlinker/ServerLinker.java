package net.year4000.serverlinker;

import com.ewized.utilities.core.util.Pinger;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.bungee.module.ModuleListeners;
import net.year4000.ducktape.core.module.ModuleInfo;
import net.year4000.serverlinker.commands.GeneralCommands;
import net.year4000.serverlinker.commands.LinkerBaseCommand;
import net.year4000.serverlinker.webserver.ServerHandler;
import net.year4000.serverlinker.webserver.StatusCollection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "ServerLinker",
    version = "1.5",
    description = "The core system that handles everything about the servers.",
    authors = {"Year4000"}
)
@ModuleListeners({Listeners.class})
public class ServerLinker extends BungeeModule {
    private static ProxyServer proxy = ProxyServer.getInstance();
    @Getter
    private static ServerLinker instance;
    @Getter
    @Setter
    private ScheduledTask updateclock;
    private LoadingCache<String, ServerInfo> lowestHub = CacheBuilder.newBuilder()
        .maximumSize(1)
        .expireAfterAccess(5, TimeUnit.SECONDS)
        .build(new CacheLoader<String, ServerInfo>() {
            @Override
            public ServerInfo load(String s) throws Exception {
                ServerInfo lowestServer = null;

                for (Server serverName : Settings.get().getServers()) {
                    //System.out.println(serverName);
                    if (!serverName.isHub()) continue;
                    ServerInfo current = ProxyServer.getInstance().getServerInfo(serverName.getName());
                    try {
                        Pinger.StatusResponse server = new Pinger(
                            current.getAddress(),
                            Pinger.TIME_OUT
                        ).fetchData();

                        if (lowestServer == null) {
                            lowestServer = current;
                        }

                        if (lowestServer.getPlayers().size() > server.getPlayers().getOnline()) {
                            lowestServer = current;
                        }
                    } catch (IOException e) {
                        // Server could be down go to next server. and log to console
                        log("The server `%s` could be down, this is a warning.", serverName);
                    }
                }

                return lowestServer;
            }
        });

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {
        // clean out servers and register with own
        proxy.getServers().clear();
        Settings.get().getServers().forEach(server -> {
            try {
                Object[] address = server.getAddress().contains(":") ? server.getAddress().split(":", 2) : new Object[]{server.getAddress(), 25565};
                ServerInfo info = proxy.constructServerInfo(
                    server.getName(),
                    new InetSocketAddress((String) address[0], Integer.valueOf((String) address[1])),
                    server.getGroup(),
                    false // not restricted
                );
                proxy.getServers().put(server.getName(), info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // commands
        registerCommand(GeneralCommands.class);
        registerCommand(LinkerBaseCommand.class);

        // the update clock
        updateclock = StatusCollection.get().updateClock();

        // the web server
        ServerHandler.startWebServer();
    }

    @Override
    public void disable() {
        ServerHandler.stopWebServer();

        updateclock.cancel();
    }

    /** Would the command sender have permissions. */
    public static boolean hasPerms(CommandSender sender) {
        return sender.hasPermission("serverlinker.admin") || sender.hasPermission("serverlinker.*");
    }

    /**
     * Gets the lowest player count server.
     * When using this check if its null, that means their is no online hubs.
     */
    public ServerInfo getLowestHub() {
        return lowestHub.getUnchecked("ServerInfo");
    }
}