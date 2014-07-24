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

        sender.sendMessage(MessageUtil.makeMessage("&6Added server &e" + serverInfo.getName()));
    }

    @Command(
        aliases = {"remove", "delete", "del"},
        desc = "Remove a BungeeCord server",
        usage = "<name>",
        min = 1
    )
    public static void delserver(final CommandContext args, CommandSender sender) throws CommandException {
        String name = args.getJoinedStrings(0);
        ServerInfo server;

        if ((server = ProxyServer.getInstance().getServers().remove(name)) == null) {
            throw new CommandException("Could not find server " + name);
        }

        StatusCollection.get().removeServer(server);
        sender.sendMessage(MessageUtil.makeMessage("&6Removed server &e" + name));
    }
}