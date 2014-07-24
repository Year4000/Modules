package net.year4000.serverlinker.commands;

import com.sk89q.minecraft.util.commands.*;
import net.md_5.bungee.api.CommandSender;

@SuppressWarnings("unused")
public final class LinkerBaseCommand {
    @Command(
        aliases = {"linker"},
        desc = "Base command to manage servers."
    )
    @NestedCommand(LinkerCommands.class)
    @CommandPermissions("serverlinker.admin")
    public static void linker(final CommandContext args, CommandSender sender) throws CommandException {}
}