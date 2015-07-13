/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.infractions;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.utilities.bungee.MessageUtil;
import net.year4000.utilities.bungee.commands.*;
import net.year4000.utilities.bungee.pagination.SimplePaginatedResult;

import java.util.Optional;

public final class Commands {

    @Command(
        aliases = {"ban"},
        usage = "[player] [message]",
        desc = "Ban a player.",
        flags = "fs",
        min = 1
    )
    @CommandPermissions({"omega", "infractions.staff", "infractions.ban"})
    public static void ban(CommandContext args, CommandSender sender) throws CommandException {
        // Set up the fields.
        String name = args.getString(0), message = "";
        Optional<ProxiedPlayer> proxiedPlayer = Optional.ofNullable(ProxyServer.getInstance().getPlayer(name));
        Optional<Player> player = Infractions.getStorage().getPlayer(name);

        // Check is player exists or force.
        if (!player.isPresent()) {
            throw new CommandException("&6" + name + " does not exists.");
        }

        Player badguy = player.get();

        // If banned don't ban again.
        if (badguy.isBanned() || badguy.isLocked()) {
            throw new CommandException("&6" + name + " already can't connect to the server.");
        }

        // Create the message
        for (int i = 1; i < args.argsLength(); i++) {
            message += args.getString(i) + " ";
        }

        // When no message is created use the default one.
        if (message.equals("")) {
            message = new Message(badguy.getLocale()).get("default.ban");
        }

        // Update the database.
        ProxiedPlayer judge = ProxyServer.getInstance().getPlayer(sender.getName());
        //System.out.println(judge.getUUID());
        if (badguy.ban(judge, message)) {
            sender.sendMessage(MessageUtil.message("Infraction could not be added to database."));
        }

        // Kick the player from the server
        final String finalMessage = message;
        proxiedPlayer.ifPresent(proxied -> proxied.disconnect(createMessage(proxied, finalMessage)));

        // Broadcast if not silent.
        if (!args.hasFlag('s')) {
            MessageUtil.broadcast("&e" + args.getString(0) + " &6has been banned: &e" + message);
        }
    }

    @Command(
            aliases = {"lock"},
            usage = "[player] [time] [message]",
            desc = "Lock a player out for a time frame.",
            flags = "fs",
            min = 1
    )
    @CommandPermissions({"omega", "infractions.staff", "infractions.lock"})
    public static void lock(CommandContext args, CommandSender sender) throws CommandException {
        //throw new CommandException("Currently Disabled util Account System is out");
        // Set up the fields.
        String name = args.getString(0), message = "";
        Optional<ProxiedPlayer> proxiedPlayer = Optional.ofNullable(ProxyServer.getInstance().getPlayer(name));
        Optional<Player> player = Infractions.getStorage().getPlayer(name);

        // Check is player exists or force.
        if (!player.isPresent()) {
            throw new CommandException("&6" + name + " does not exists.");
        }

        Player badguy = player.get();

        // If banned don't ban again.
        if (badguy.isBanned() || badguy.isLocked()) {
            throw new CommandException("&6" + name + " already can't connect to the server.");
        }

        if (args.argsLength() < 2) {
            throw new CommandException("You must supply a valid time frame.");
        }

        // Create the message
        for (int i = 2; i < args.argsLength(); i++) {
            message += args.getString(i) + " ";
        }

        // When no message is created use the default one.
        if (message.equals("")) {
            message = new Message(badguy.getLocale()).get("default.lock");
        }

        // Update the database, catch exception from Time Duration
        try {
            TimeDuration duration = TimeDuration.getFromString(args.getString(1));
            ProxiedPlayer judge = ProxyServer.getInstance().getPlayer(sender.getName());

            if (!badguy.lock(judge, message, duration.toSecs())) {
                sender.sendMessage(MessageUtil.message("Infraction could not be added to database."));
            }
        }
        catch (Exception e) {
            throw new CommandException("The time duration could not be parsed.");
        }

        // Kick the player from the server
        final String finalMessage = message;
        proxiedPlayer.ifPresent(proxied -> proxied.disconnect(createMessage(proxied, finalMessage)));

        // Broadcast if not silent.
        if (!args.hasFlag('s')) {
            MessageUtil.broadcast("&e" + args.getString(0) + " &6has been locked: &e" + message);
        }
    }

    @Command(
            aliases = {"kick"},
            usage = "[player] [message]",
            desc = "Kick a player.",
            flags = "s",
            min = 1
    )
    @CommandPermissions({"omega", "infractions.staff", "infractions.kick"})
    public static void kick(CommandContext args, CommandSender sender) throws CommandException {
        // Set up the fields.
        String name = args.getString(0), message = "";
        Optional<ProxiedPlayer> proxiedPlayer = Optional.ofNullable(ProxyServer.getInstance().getPlayer(name));
        Optional<Player> player = Infractions.getStorage().getPlayer(name);

        // Check is player exists or force.
        if (!player.isPresent()) {
            throw new CommandException("&6" + name + " does not exists.");
        }

        Player badguy = player.get();

        // Create the message
        for (int i = 1; i < args.argsLength(); i++) {
            message += args.getString(i) + " ";
        }

        // When no message is created use the default one.
        if (message.equals("")) {
            message = new Message(badguy.getLocale()).get("default.kick");
        }

        // Update the database.
        ProxiedPlayer judge = ProxyServer.getInstance().getPlayer(sender.getName());
        //System.out.println(judge.getUUID());
        if (badguy.kick(judge, message)) {
            sender.sendMessage(MessageUtil.message("Infraction could not be added to database."));
        }
        // Kick the player from the server
        final String finalMessage = message;
        proxiedPlayer.ifPresent(proxied -> proxied.disconnect(createMessage(proxied, finalMessage)));

        // Broadcast if not silent.
        if (!args.hasFlag('s')) {
            MessageUtil.broadcast("&e" + args.getString(0) + " &6has been kicked: &e" + message);
        }
    }

    /**
     * Create a disconnect message to tell the user their account can't login.
     * @param player The player's name.
     * @param message The message to show.
     * @return Disconnect message.
     */
    private static BaseComponent[] createMessage(ProxiedPlayer player, String message) {
        String link = Settings.get().getLink().replaceAll("%player%", player.getName());
        return MessageUtil.message(message + "\n\n" + new Message(player).get("default.notice") + "\n" + link);
    }
}
