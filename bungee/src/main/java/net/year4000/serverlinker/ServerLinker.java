package net.year4000.serverlinker;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.bungee.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.serverlinker.commands.GeneralCommands;
import net.year4000.serverlinker.commands.LinkerBaseCommand;
import net.year4000.serverlinker.webserver.ServerHandler;
import net.year4000.serverlinker.webserver.ServerStatus;
import net.year4000.serverlinker.webserver.StatusCollection;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "ServerLinker",
    version = "1.7",
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
    private static Set<String> VIPS = ImmutableSet.of("theta", "mu", "pi", "sigma", "phi", "delta", "omega");
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

                    if (current == null) {
                        log(serverName.getName() + " is not in the server list");
                        continue;
                    }

                    try {
                        ServerStatus server = StatusCollection.get().getServers().get(serverName.getName());

                        if (lowestServer == null) {
                            lowestServer = current;
                        }

                        if (lowestServer.getPlayers().size() > server.getStatus().getPlayers().getOnline()) {
                            lowestServer = current;
                        }
                    } catch (NullPointerException e) {
                        // Server could be down go to next server. and log to console
                        log("The server `%s` could be down, this is a warning.", serverName.getName());
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
        return isVIP(sender) || sender.hasPermission("serverlinker.admin") || sender.hasPermission("serverlinker.*");
    }

    /** Is the selected player a VIP */
    public static boolean isVIP(CommandSender player) {
        for (String permission : VIPS) {
            if (player.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the lowest player count server.
     * When using this check if its null, that means their is no online hubs.
     */
    public ServerInfo getLowestHub() {
        return lowestHub.getUnchecked("ServerInfo");
    }
}