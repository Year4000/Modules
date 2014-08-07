package net.year4000.serverlist.commands;

import net.md_5.bungee.api.CommandSender;
import net.year4000.serverlist.Settings;
import net.year4000.serverlist.messages.Message;
import net.year4000.serverlist.messages.MessageFactory;
import net.year4000.utilities.bungee.MessageUtil;
import net.year4000.utilities.bungee.commands.BungeeWrappedCommandSender;
import net.year4000.utilities.bungee.commands.Command;
import net.year4000.utilities.bungee.commands.CommandContext;
import net.year4000.utilities.bungee.commands.CommandException;
import net.year4000.utilities.bungee.pagination.SimplePaginatedResult;

public final class ListSub {
    @Command(
        aliases = {"list", "view", "show"},
        desc = "List the messages for the ping list",
        usage = "(page)",
        flags = "r"
    )
    public static void list(final CommandContext args, CommandSender sender) throws CommandException {
        MessageFactory factory = new MessageFactory();
        final int MAX_PER_PAGE = 8;

        new SimplePaginatedResult<String>(MessageUtil.replaceColors(new Settings().getPrefix()), MAX_PER_PAGE) {
            @Override
            public String format(String msg, int index) {
                try {
                    String message = args.hasFlag('r') ? msg : new Message(sender).get(msg);
                    return MessageUtil.replaceColors((index + 1) + " - " + message);
                } catch (Exception e) {
                    return MessageUtil.replaceColors("&c" + e.getMessage());
                }
            }
        }.display(
            new BungeeWrappedCommandSender(sender),
            factory.getMessages(),
            args.argsLength() > 0 ? args.getInteger(0) : 1
        );
    }
}
