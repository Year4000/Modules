package net.year4000.serverlist.commands;

import net.md_5.bungee.api.CommandSender;
import net.year4000.utilities.bungee.commands.*;

public final class ListBase {
    @Command(
        aliases = {"serverlist"},
        desc = "ServerList base command."
    )
    @NestedCommand({ListSub.class})
    @CommandPermissions({"omega", "serverlist.admin", "serverlist.list"})
    public static void announcer(CommandContext args, CommandSender sender) throws CommandException {
        // empty
    }
}
