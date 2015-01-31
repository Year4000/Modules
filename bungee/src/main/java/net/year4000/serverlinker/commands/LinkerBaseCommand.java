package net.year4000.serverlinker.commands;

import net.md_5.bungee.api.CommandSender;
import net.year4000.utilities.bungee.commands.*;

@SuppressWarnings("unused")
public final class LinkerBaseCommand {
    @Command(
        aliases = {"linker"},
        desc = "Base command to manage servers."
    )
    @NestedCommand(LinkerCommands.class)
    @CommandPermissions({"omega", "serverlinker.admin"})
    public static void linker(final CommandContext args, CommandSender sender) throws CommandException {}
}