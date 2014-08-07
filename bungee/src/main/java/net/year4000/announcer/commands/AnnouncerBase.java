package net.year4000.announcer.commands;

import net.md_5.bungee.api.CommandSender;
import net.year4000.utilities.bungee.commands.Command;
import net.year4000.utilities.bungee.commands.CommandContext;
import net.year4000.utilities.bungee.commands.CommandException;
import net.year4000.utilities.bungee.commands.NestedCommand;

public final class AnnouncerBase {
    @Command(
        aliases = {"announcer", "broadcaster", "announce"},
        desc = "Announcer base command."
    )
    @NestedCommand({AnnouncerSub.class})
    public static void announcer(CommandContext args, CommandSender sender) throws CommandException {
        // empty
    }
}
