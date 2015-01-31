package net.year4000.announcer.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.announcer.Announcer;
import net.year4000.announcer.Broadcaster;
import net.year4000.announcer.Settings;
import net.year4000.announcer.messages.InMessage;
import net.year4000.utilities.bungee.MessageUtil;
import net.year4000.utilities.bungee.commands.*;
import net.year4000.utilities.bungee.pagination.SimplePaginatedResult;
import net.year4000.utilities.config.InvalidConfigurationException;

public final class AnnouncerSub {
    private static Settings settings = Settings.get();

    @Command(
        aliases = {"reload", "refresh"},
        desc = "Reload the config.",
        min = 0,
        max = 1
    )
    @CommandPermissions({"omega", "announcer.admin", "announcer.reload"})
    public static void reload(CommandContext args, CommandSender sender) throws CommandException {
        ProxiedPlayer player = sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null;
        InMessage locale = new InMessage(player);

        Announcer.getInst().reloadSchedulers();
        sender.sendMessage(MessageUtil.message(locale.get("cmd.reload")));
    }

    @Command(
        aliases = {"list", "view", "show"},
        desc = "List the messages for the specify server.",
        usage = "[server|global] (page)",
        flags = "r",
        min = 1,
        max = 2
    )
    @CommandPermissions({"omega", "announcer.admin", "announcer.list"})
    public static void list(final CommandContext args, CommandSender sender) throws CommandException {
        ProxiedPlayer player = sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null;
        InMessage locale = new InMessage(player);

        try {
            // Show raw messages or pretty messages.
            final int MAX_PER_PAGE = 8;
            new SimplePaginatedResult<String>(locale.get("cmd.messages", args.getString(0)), MAX_PER_PAGE) {
                @Override
                public String format(String server, int index) {
                    try {
                        String message = args.hasFlag('r') ? server : TextComponent.toLegacyText(Broadcaster.parseBroadcast(player, server));
                        return MessageUtil.replaceColors((index + 1) + " - " + message);
                    } catch (Exception e) {
                        return MessageUtil.replaceColors("&c" + e.getMessage());
                    }
                }
            }.display(
                new BungeeWrappedCommandSender(sender),
                settings.getServerMessages(args.getString(0)),
                args.argsLength() == 2 ? args.getInteger(1) : 1
            );
        } catch (NullPointerException e) {
            throw new CommandException(locale.get("cmd.server.not_found"));
        }
    }

    @Command(
        aliases = {"add", "create"},
        desc = "Add a message to a server.",
        usage = "[server|global] [messages]",
        min = 2
    )
    @CommandPermissions({"omega", "announcer.admin", "announcer.edit", "announcer.add"})
    public static void add(CommandContext args, CommandSender sender) throws CommandException {
        ProxiedPlayer player = sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null;
        InMessage locale = new InMessage(player);

        try {
            settings.addServerMessages(args.getString(0), args.getJoinedStrings(1));
            sender.sendMessage(MessageUtil.message(locale.get("cmd.message.add", args.getString(0))));
        } catch (NullPointerException e) {
            throw new CommandException(locale.get("cmd.server.not_found"));
        } catch (InvalidConfigurationException e) {
            throw new CommandException(locale.get("cmd.message.add.error"));
        }
    }

    @Command(
        aliases = {"remove", "delete", "del"},
        desc = "Remove a message from a server.",
        usage = "[server|global] [position]",
        min = 2,
        max = 2
    )
    @CommandPermissions({"omega", "announcer.admin", "announcer.edit", "announcer.remove"})
    public static void remove(CommandContext args, CommandSender sender) throws CommandException {
        ProxiedPlayer player = sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null;
        InMessage locale = new InMessage(player);

        try {
            if (args.getInteger(1) < 0 || args.getInteger(1) > settings.getServerMessages(args.getString(0)).size()) {
                throw new CommandException(locale.get("cmd.message.no_index"));
            } else {
                settings.removeServerMessages(args.getString(0), args.getInteger(1) - 1);
                sender.sendMessage(MessageUtil.message(locale.get("cmd.message.remove", args.getString(0))));
            }
        } catch (NullPointerException e) {
            throw new CommandException(locale.get("cmd.server.not_found"));
        } catch (InvalidConfigurationException e) {
            throw new CommandException(locale.get("cmd.message.remove.error"));
        }
    }

    @Command(
        aliases = {"setting", "option"},
        desc = "Change a setting or view it in the config.",
        usage = "[setting] [option]",
        min = 1
    )
    @CommandPermissions({"omega", "announcer.admin", "announcer.edit", "announcer.setting"})
    public static void setting(CommandContext args, CommandSender sender) throws CommandException {
        ProxiedPlayer player = sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null;
        InMessage locale = new InMessage(player);

        // View the settings.
        if (args.argsLength() == 1) {
            String message;
            switch (args.getString(0)) {
                case "delay":
                    message = locale.get("cmd.config.delay", settings.getDelay());
                    break;
                case "prefix":
                    message = locale.get("cmd.config.prefix", settings.getPrefix());
                    break;
                case "random":
                    message = locale.get("cmd.config.random", settings.isRandom());
                    break;
                case "messagesurl":
                    message = locale.get("cmd.config.messagesurl", settings.getMessagesURL());
                    break;
                case "internalurl":
                    message = locale.get("cmd.config.internalurl", settings.getInternalURL());
                    break;
                default:
                    message = locale.get("cmd.config.not_found");
            }
            sender.sendMessage(MessageUtil.message(message));
        }
        // Change the setting of option
        else {
            try {
                String message;
                switch (args.getString(0)) {
                    case "delay":
                        message = locale.get("cmd.config.delay", settings.setSetting("delay", args.getInteger(1)));
                        Announcer.getInst().reloadSchedulers();
                        break;
                    case "prefix":
                        message = locale.get("cmd.config.prefix", settings.setSetting("prefix", args.getString(1)));
                        break;
                    case "random":
                        message = locale.get("cmd.config.random", settings.setSetting("random", args.getString(1).equalsIgnoreCase("true")));
                        break;
                    case "messagesurl":
                        message = locale.get("cmd.config.messages_url", settings.setSetting("messagesURL", args.getString(1)));
                        break;
                    case "internalurl":
                        message = locale.get("cmd.config.internal_url", settings.setSetting("internalURL", args.getString(1)));
                        break;
                    default:
                        message = locale.get("cmd.config.not_found");
                }
                sender.sendMessage(MessageUtil.message(message));
            } catch (InvalidConfigurationException e) {
                throw new CommandException(locale.get("cmd.config.error"));
            }
        }
    }
}
