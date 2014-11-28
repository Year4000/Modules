package net.year4000.infractions;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.utilities.bungee.MessageUtil;
import net.year4000.utilities.bungee.commands.*;
import net.year4000.utilities.bungee.pagination.SimplePaginatedResult;

public final class Commands {

    private static String[] recordTypes = new String[]{
            "Ban", "Lock", "Kick"
    };

    @Command(aliases = {"bans"}, desc = "")
    @CommandPermissions({"infractions.staff", "infractions.ban"})
    public static void bans(CommandContext args, CommandSender sender) throws CommandException {
        Infractions.getStorage().read(FileStorage.STORAGE);
        sender.sendMessage(MessageUtil.message("&6Infractions storage file is reloaded."));
    }

    @Command(
            aliases = {"infractions", "infrac"},
            usage = "[player] [page]",
            desc = "Get all the infractions of a player",
            min = 1
    )
    @CommandPermissions({"infractions.staff", "infractions.infractions"})
    public static void infractions(CommandContext args, CommandSender sender) throws CommandException {
        String name = args.getString(0);
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);

        if(player == null && !Infractions.getStorage().hasUUID(name))
            throw new CommandException("&6" + name + " is not online");

        Player badguy = new Player(player == null ? Infractions.getStorage().getUUID(name) : player.getUniqueId(), name);

        if(badguy.getRecord().getRecords().size() == 0)
            throw new CommandException("&6" + name + " has no infractions");

        final int MAX_PER_PAGE = 8;
        new SimplePaginatedResult<InfractionRecord>("&e" + name + "&6's records", MAX_PER_PAGE){
            @Override
            public String format(InfractionRecord record, int index){
                return "&6" + recordTypes[record.getType()] + " &eby &6" + record.getJudge() + " &eat &6" + record.getTime();
            }
        }.display(
                new BungeeWrappedCommandSender(sender),
                badguy.getRecord().getRecords(),
                args.argsLength() == 2 ? args.getInteger(1) : 1
        );
    }

    @Command(
        aliases = {"ban"},
        usage = "[player] [message]",
        desc = "Ban a player.",
        flags = "fs",
        min = 1
    )
    @CommandPermissions({"infractions.staff", "infractions.ban"})
    public static void ban(CommandContext args, CommandSender sender) throws CommandException {
        // Set up the fields.
        String name = args.getString(0), message = "";
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);

        // Check is player exists or force.
        if (player == null && !Infractions.getStorage().hasUUID(name) /*&& !args.hasFlag('f')*/)
            throw new CommandException("&6" + name + " is not online.");

        Player badguy = new Player(player == null ? Infractions.getStorage().getUUID(name) : player.getUniqueId(), name);

        // If banned don't ban again.
        if (badguy.isBanned() || badguy.isLocked())
            throw new CommandException("&6" + name + " already can't connect to the server.");

        // Create the message
        for (int i = 1; i < args.argsLength(); i++)
            message += args.getString(i) + " ";

        // When no message is created use the default one.
        if (message.equals(""))
            message = new Message(player == null ? sender : player).get("default.ban");

        // Update the database.
        ProxiedPlayer judge = ProxyServer.getInstance().getPlayer(sender.getName());
        //System.out.println(judge.getUUID());
        if (badguy.ban(judge, message)) {
            sender.sendMessage(MessageUtil.message("Infraction could not be added to database."));
        }

        // Kick the player from the server
        if (player != null) player.disconnect(createMessage(player, message));

        // Broadcast if not silent.
        if (!args.hasFlag('s'))
            MessageUtil.broadcast("&e" + args.getString(0) + " &6has been banned: &e" + message);
    }

    @Command(
            aliases = {"lock"},
            usage = "[player] [time] [message]",
            desc = "Lock a player out for a time frame.",
            flags = "fs",
            min = 1
    )
    @CommandPermissions({"infractions.staff", "infractions.lock"})
    public static void lock(CommandContext args, CommandSender sender) throws CommandException {
        //throw new CommandException("Currently Disabled util Account System is out");
        // Set up the fields.
        String name = args.getString(0), message = "";
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);

        // Check is player exists or force
        if (player == null && !Infractions.getStorage().hasUUID(name) /*&& !args.hasFlag('f')*/)
            throw new CommandException("&6" + name + " is not online.");

        Player badguy = new Player(player == null ? Infractions.getStorage().getUUID(name) : player.getUniqueId(), name);

        // If banned don't ban again.
        if (badguy.isBanned() || badguy.isLocked())
            throw new CommandException("&6" + name + " already can't connect to the server.");

        if (args.argsLength() < 2)
            throw new CommandException("You must supply a valid time frame.");

        // Create the message
        for (int i = 2; i < args.argsLength(); i++)
            message += args.getString(i) + " ";

        // When no message is created use the default one.
        if (message.equals(""))
            message = new Message(player == null ? sender : player).get("default.lock");

        // Update the database, catch exception from Time Duration
        try {
            TimeDuration duration = TimeDuration.getFromString(args.getString(1));
            ProxiedPlayer judge = ProxyServer.getInstance().getPlayer(sender.getName());

            if (!badguy.lock(judge, message, duration.toSecs())) {
                sender.sendMessage(MessageUtil.message("Infraction could not be added to database."));
            }
        } catch (Exception e) {
            throw new CommandException("The time duration could not be parsed.");
        }

        // Kick the player from the server
        if (player != null) player.disconnect(createMessage(player, message));

        // Broadcast if not silent.
        if (!args.hasFlag('s'))
            MessageUtil.broadcast("&e" + args.getString(0) + " &6has been locked: &e" + message);
    }

    @Command(
            aliases = {"kick"},
            usage = "[player] [message]",
            desc = "Kick a player.",
            flags = "s",
            min = 1
    )
    @CommandPermissions({"infractions.staff", "infractions.kick"})
    public static void kick(CommandContext args, CommandSender sender) throws CommandException {
        // Set up the fields.
        String name = args.getString(0), message = "";
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);

        // Check is player exists or force
        if (player == null)
            throw new CommandException("&6" + name + " is not online.");

        // Create the message
        for (int i = 1; i < args.argsLength(); i++)
            message += args.getString(i) + " ";

        // When no message is created use the default one.
        if (message.equals(""))
            message = new Message(player).get("default.kick");

        // Update the database.
        ProxiedPlayer judge = ProxyServer.getInstance().getPlayer(sender.getName());
        new Player(player).kick(judge, message);

        // Kick the player from the server
        player.disconnect(createMessage(player, message));

        // Broadcast if not silent.
        if (!args.hasFlag('s'))
            MessageUtil.broadcast("&e" + args.getString(0) + " &6has been kicked: &e" + message);
    }

    /**
     * Create a disconnect message to tell the user their account can't login.
     * @param player The player's name.
     * @param message The message to show.
     * @return Disconnect message.
     */
    private static BaseComponent[] createMessage(ProxiedPlayer player, String message) {
        String link = Config.get().getLink().replaceAll("%player%", player.getName());
        return MessageUtil.message(message + "\n\n" + new Message(player).get("default.notice") + "\n" + link);
    }
}
