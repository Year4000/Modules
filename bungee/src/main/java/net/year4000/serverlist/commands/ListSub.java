package net.year4000.serverlist.commands;

import com.ewized.utilities.bungee.util.MessageUtil;
import com.sk89q.bungee.util.BungeeWrappedCommandSender;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.pagination.SimplePaginatedResult;
import net.md_5.bungee.api.CommandSender;
import net.year4000.serverlist.Settings;
import net.year4000.serverlist.messages.Message;
import net.year4000.serverlist.messages.MessageFactory;

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

        new SimplePaginatedResult<String>(MessageUtil.message(new Settings().getPrefix()), MAX_PER_PAGE) {
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
