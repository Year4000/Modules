package net.year4000.serverlinker.commands;

import com.ewized.utilities.bungee.util.MessageUtil;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.year4000.serverlinker.Server;
import net.year4000.serverlinker.Settings;
import net.year4000.serverlinker.messages.Message;
import net.year4000.serverlinker.webserver.StatusCollection;

import java.net.InetSocketAddress;

@SuppressWarnings("unused")
public final class LinkerCommands {
    @Command(
        aliases = {"add"},
        desc = "Add a BungeeCord server",
        usage = "<name> <address>",
        flags = "rhp:g:",
        min = 2
    )
    public static void addserver(final CommandContext args, CommandSender sender) throws CommandException {
        Message locale = new Message(sender);
        String name = args.getString(0);
        String address = args.getString(1);
        String group = args.hasFlag('g') ? args.getFlag('g') : name;
        int port = args.hasFlag('p') ? args.getFlagInteger('p') : 25565;
        boolean restricted = args.hasFlag('r');
        boolean hub = args.hasFlag('h');

        // add entry to config and save
        Server info = new Server();
        info.setName(name);
        info.setGroup(group);
        info.setAddress(address + ":" + port);
        info.setHub(hub);
        Settings.get().getServers().add(info);

        // add to internal servers
        ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(
            name,
            new InetSocketAddress(address, port),
            group,
            restricted
        );

        ProxyServer.getInstance().getServers().put(name, serverInfo);

        StatusCollection.get().addServer(serverInfo);

        sender.sendMessage(MessageUtil.makeMessage(locale.get("linker.add", serverInfo.getName())));
    }

    @Command(
        aliases = {"remove", "delete", "del"},
        desc = "Remove a BungeeCord server",
        usage = "<name>",
        min = 1
    )
    public static void delserver(final CommandContext args, CommandSender sender) throws CommandException {
        Message locale = new Message(sender);
        String name = args.getJoinedStrings(0);
        ServerInfo server;

        if ((server = ProxyServer.getInstance().getServers().remove(name)) == null) {
            throw new CommandException(locale.get("linker.no_found",  name));
        }

        StatusCollection.get().removeServer(server);
        sender.sendMessage(MessageUtil.makeMessage(locale.get("linker.remove", name)));
    }
}