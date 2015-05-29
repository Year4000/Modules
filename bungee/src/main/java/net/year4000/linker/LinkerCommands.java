package net.year4000.linker;

import com.google.common.base.Ascii;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.linker.messages.Message;
import net.year4000.utilities.Pinger;
import net.year4000.utilities.bungee.MessageUtil;
import net.year4000.utilities.bungee.commands.*;
import net.year4000.utilities.bungee.pagination.SimplePaginatedResult;
import net.year4000.utilities.sdk.routes.players.PlayerCountJson;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public class LinkerCommands {
    private static final int MAX_PER_PAGE = 8;
    private static ProxyServer proxy = ProxyServer.getInstance();

    @Command(
        aliases = {"servers"},
        desc = "View the server's you can connect to.",
        usage = "<page>",
        flags = "ah",
        min = 0,
        max = 1
    )
    public static void list(final CommandContext args, final CommandSender sender) throws CommandException {
        new SimplePaginatedResult<ServerRoute.ServerJsonKey>("&3[&bYear4000&3]", MAX_PER_PAGE) {
            @Override
            public String formatHeader(int page, int maxPages) {
                //PlayerCountJson.Count networkCount = Linker.instance.api.getPlayerCount().getNetworkPlayerCount();
                String htop = String.format(
                    "&7&m******&a %s &7(&a%s&7) &2[page &a%s&2/&a%s&2] &7&m******",
                    header,
                    Linker.proxy.getOnlineCount(),
                    page,
                    maxPages
                );
                return MessageUtil.replaceColors(htop);
            }

            @Override
            public String format(ServerRoute.ServerJsonKey server, int index) {
                InetSocketAddress info = proxy.getServers().get(server.getName()).getAddress();

                boolean online = !(server.getStatus() == null || server.getStatus().getPlayers() == null);
                String serverList = (online ? "&2" : "&c") + server.getName();

                // show player count if online
                if (online) {
                    Pinger.Players players = server.getStatus().getPlayers();
                    serverList += String.format(
                        " &7(&a%s&8/&a%s&7) ",
                        players.getOnline(),
                        players.getMax()
                    );

                    // don't show motd if its y4k branded
                    if (!MessageUtil.stripColors(server.getStatus().getDescription()).contains("[Y4K]")) {
                        serverList += Ascii.truncate(server.getStatus().getDescription(), 42, "&7...");
                    }
                }

                String address = !(args.hasFlag('a') && Linker.isStaff(sender)) ? "" : String.format(
                    "\n%s&7\\[ &6%s&7:&6%s",
                    ((index + 1) + " - ").replaceAll(".", " "),
                    info.getHostName(),
                    info.getPort()
                );

                return MessageUtil.replaceColors((index + 1) + " &7-&r " + serverList + address);
            }

            // Override this one so we can handle new line chars better
            @Override
            public void display(WrappedCommandSender sender, List<? extends ServerRoute.ServerJsonKey> results, int page) throws CommandException {
                if (results.size() == 0) throw new CommandException("No results match!");

                int maxPages = results.size() / this.resultsPerPage + 1;

                // If the content divides perfectly, eg (18 entries, and 9 per page)
                // we end up with a blank page this handles this case
                if (results.size() % this.resultsPerPage == 0) {
                    maxPages--;
                }

                if (page <= 0 || page > maxPages) throw new CommandException("Unknown page selected! " + maxPages + " total pages.");

                sender.sendMessage(this.formatHeader(page, maxPages));
                for (int i = this.resultsPerPage * (page - 1); i < this.resultsPerPage * page && i < results.size(); i++) {
                    sender.sendMessage(this.format(results.get(i), i).split("\\n"));
                }
            }

        }.display(
            new BungeeWrappedCommandSender(sender),
            Linker.instance.servers.values().stream()
                .filter(s -> !s.getName().startsWith(".") || (args.hasFlag('h')  && Linker.isStaff(sender)))
                .collect(Collectors.toList()),
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
            player.sendMessage(MessageUtil.message(locale.get("server.on", player.getServer().getInfo().getName())));
            player.sendMessage(MessageUtil.message(locale.get("server.use")));
        }
        else {
            String serverName = args.getJoinedStrings(0);
            ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);

            if (server == null) {
                throw new CommandException(locale.get("server.no_name", serverName));
            }

            // If server starts with . must be VIP or higer to connect
            if (!server.canAccess(player) && !(Linker.isVIP(player) || Linker.isStaff(player))) {
                throw new CommandException(locale.get("server.no_name", serverName));
            }

            player.sendMessage(MessageUtil.message(locale.get("server.connect", server.getName())));
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
        ServerInfo lowest = Linker.instance.getHub();
        Message locale = new Message(player);

        if (lowest == null) {
            player.sendMessage(MessageUtil.message(locale.get("hub.none")));
        }
        else if (player.getServer().getInfo().getName().toLowerCase().contains("hub")) {
            player.sendMessage(MessageUtil.message(locale.get("hub.on")));
        }
        else {
            player.sendMessage(MessageUtil.message(locale.get("server.connect", lowest.getName())));
            player.connect(lowest);
        }
    }
}