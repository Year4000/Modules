package net.year4000.serverlist.commands;

import com.sk89q.minecraft.util.commands.*;
import net.md_5.bungee.api.CommandSender;

public final class ListBase {
    @Command(
        aliases = {"serverlist"},
        desc = "ServerList base command."
    )
    @NestedCommand({ListSub.class})
    @CommandPermissions({"serverlist.admin", "serverlist.list"})
    public static void announcer(CommandContext args, CommandSender sender) throws CommandException {
        // empty
    }
}
