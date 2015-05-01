package net.year4000.linker;

import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.bungee.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.sdk.API;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ModuleInfo(
    name = "Linker",
    description = "Links BungeeCord with Year4000's API",
    version = "1.0",
    authors = {"Year4000"}
)
@ModuleListeners({LinkerListener.class})
public class Linker extends BungeeModule {
    public static ProxyServer proxy = ProxyServer.getInstance();
    public static Linker instance;
    private static String API_KEY = System.getProperty("Y4K_KEY");
    public API api = new API(API_KEY);
    private Map<String, ServerRoute.ServerJsonKey> servers;

    @Override
    public void enable() {
        instance = this;

        if (API_KEY == null) {
            throw new RuntimeException("Year4000 API key is null, this cant happen");
        }

        proxy.getScheduler().schedule(DuckTape.get(), this::fetchAndUpdateServers, 0, 2, TimeUnit.SECONDS);
    }

    /** Fetch the API and populate this instance with the servers */
    public void fetchAndUpdateServers() {
        Type type = new TypeToken<Map<String, ServerRoute.ServerJsonKey>>() {}.getType();
        api.getRouteAsync(ServerRoute.class, type, "servers", (data, error) -> {
            if (error == null) {
                servers = data.getServersMap();
                proxy.getServers().clear();
                servers.forEach((key, value) -> {
                    ServerInfo info = createServerInfo(value);
                    proxy.getServers().put(key, info);
                    debug("Server Found: " + info.toString());
                });
            }
        });
    }

    /** Construct a BUngeeCord ServerInfo from API response */
    public ServerInfo createServerInfo(ServerRoute.ServerJsonKey server) {
        String motd = server.getStatus() == null ? "offline" : server.getStatus().getDescription();
        InetSocketAddress address = new InetSocketAddress(server.getHostname(), server.getPort());
        boolean restricted = server.getName().startsWith(".") || server.getGroup().getDisplay().startsWith(".");
        return proxy.constructServerInfo(server.getName(), address, motd, restricted);
    }

    public ServerInfo getHub() {
        List<ServerRoute.ServerJsonKey> hubs = servers.values().stream()
                .filter(info -> info.getGroup().getName().contains("hub"))
                .filter(info -> info.getStatus() != null)
                .collect(Collectors.toList());

        ServerRoute.ServerJsonKey last = null;

        for (ServerRoute.ServerJsonKey info : hubs) {
            if (last == null) {
                last = info;
            }

            int lastOnline = last.getStatus().getPlayers().getOnline();
            int currentOnline = info.getStatus().getPlayers().getOnline();
            if (lastOnline > currentOnline) {
                last = info;
            }
        }

        return createServerInfo(last);
    }
}