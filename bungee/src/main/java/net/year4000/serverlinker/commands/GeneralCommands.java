package net.year4000.serverlinker.commands;

import com.ewized.utilities.bungee.util.MessageUtil;
import com.ewized.utilities.core.util.Pinger;
import com.sk89q.bungee.util.BungeeWrappedCommandSender;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.pagination.SimplePaginatedResult;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.serverlinker.ServerLinker;
import net.year4000.serverlinker.Settings;
import net.year4000.serverlinker.messages.Message;
import net.year4000.serverlinker.webserver.ServerStatus;
import net.year4000.serverlinker.webserver.StatusCollection;

import java.net.InetSocketAddress;

@SuppressWarnings("unused")
public final class GeneralCommands {
    private static ProxyServer proxy = ProxyServer.getInstance();

    @Command(
        aliases = {"servers"},
        desc = "View the server's you can connect to.",
        usage = "<page>",
        flags = "a",
        min = 0,
        max = 1
    )
    public static void list(final CommandContext args, final CommandSender sender) throws CommandException {
        final int MAXPERPAGE = 8;
        new SimplePaginatedResult<ServerStatus>(Settings.get().getNetwork(), MAXPERPAGE) {
            @Override
            public String formatHeader(int page, int maxPages) {
                String htop = String.format(
                    "&7&m******&a %s &7(&a%s&8/&a%s&7) &2[page &a%s&2/&a%s&2] &7&m******",
                    header,
                    proxy.getPlayers().size(),
                    proxy.getConfig().getListeners().stream().findAny().get().getMaxPlayers(),
                    page,
                    maxPages
                );
                return MessageUtil.replaceColors(htop);
            }

            @Override
            public String format(ServerStatus server, int index) {
                InetSocketAddress info = proxy.getServers().get(server.getName()).getAddress();

                String address = !(args.hasFlag('a') && ServerLinker.hasPerms(sender)) ? "" : String.format(
                    " &e%s&7:&e%s",
                    info.getHostName(),
                    info.getPort()
                );

                boolean online = !(server.getStatus() == null || server.getStatus().getPlayers() == null);
                String serverList = (online ? "&2" : "&c") + server.getName();

                // show player count if online
                if (online) {
                    Pinger.Players players = server.getStatus().getPlayers();
                    serverList += String.format(
                        " &7(&a%s&8/&a%s&7)",
                        players.getOnline(),
                        players.getMax()
                    );
                }

                return MessageUtil.replaceColors((index + 1) + " &7-&r " + serverList + address);
            }
        }.display(
            new BungeeWrappedCommandSender(sender),
            StatusCollection.get().getServers().values(),
            args.argsLength() == 1 ? args.getInteger(0) : 1
        );
    }

    @Command(
        aliases = {"server"},
        desc = "Connect to different servers",
        min = 0
    )
    public static void send(final CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof ProxiedPlayer)) {
            throw new CommandException("You must be a player to run this command.");
        }

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(sender.getName());
        Message locale = new Message(player);

        if (args.argsLength() == 0) {
            player.sendMessage(MessageUtil.makeMessage(locale.get("server.on", player.getServer().getInfo().getName())));
            player.sendMessage(MessageUtil.makeMessage(locale.get("server.use")));
        }
        else {
            String serverName = args.getJoinedStrings(0);
            ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);

            if (server == null) {
                throw new CommandException(locale.get("server.no_name", serverName));
            }

            player.sendMessage(MessageUtil.makeMessage(locale.get("server.connect", server.getName())));
            player.connect(server);
        }
    }

    @Command(
        aliases = {"hub", "lobby"},
        desc = "Connect to a hub server.",
        max = 0
    )
    public static void hub(final CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof ProxiedPlayer)) {
            throw new CommandException("&6You must be a player to run this command.");
        }

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(sender.getName());
        ServerInfo lowest = ServerLinker.getInstance().getLowestHub();
        Message locale = new Message(player);

        if (lowest == null) {
            player.sendMessage(MessageUtil.makeMessage(locale.get("hub.none")));
        }
        else if (Settings.get().getServer(player.getServer().getInfo().getName()).isHub()) {
            player.sendMessage(MessageUtil.makeMessage(locale.get("hub.on")));
        }
        else {
            player.sendMessage(MessageUtil.makeMessage(locale.get("server.connect", lowest.getName())));
            player.connect(lowest);
        }
    }
}