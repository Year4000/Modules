package net.year4000.ramtweaks;


import net.md_5.bungee.api.CommandSender;
import net.year4000.ramtweaks.messages.Message;
import net.year4000.ramtweaks.messages.ShutdownMessage;
import net.year4000.utilities.bungee.MessageUtil;
import net.year4000.utilities.bungee.commands.Command;
import net.year4000.utilities.bungee.commands.CommandContext;
import net.year4000.utilities.bungee.commands.CommandException;
import net.year4000.utilities.bungee.commands.CommandPermissions;

import java.lang.management.ManagementFactory;

public final class RamCommands {
    @Command(
        aliases = {"grestart"},
        desc = "Restart the network with a friendly 10 sec delay.",
        flags = "t:",
        max = 1
    )
    @CommandPermissions({"eramtweaks.restart"})
    public static void restart(CommandContext args, CommandSender sender) throws CommandException {
        new ShutdownMessage(args.hasFlag('t') ? args.getFlagInteger('t') : 10);
    }

    @Command(
        aliases = {"guptime"},
        desc = "Get the uptime of the network."
    )
    @CommandPermissions({"eramtweaks.uptime"})
    public static void uptime(CommandContext args, CommandSender sender) throws CommandException {
        long starttime = ManagementFactory.getRuntimeMXBean().getStartTime();

        sender.sendMessage(MessageUtil.message(new Message(sender).get(
            "cmd.uptime",
            DateUtil.formatDateDiff(starttime)
        )));
    }

    @Command(
        aliases = {"ginfo"},
        desc = "Get server information"
    )
    @CommandPermissions({"eramtweaks.info"})
    public static void serverInfo(CommandContext args, CommandSender sender) throws CommandException {
        Message locale = new Message(sender);
        Runtime rt = Runtime.getRuntime();

        sender.sendMessage(MessageUtil.message(locale.get(
            "cmd.info.system",
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch"))));

        sender.sendMessage(MessageUtil.message(locale.get(
            "cmd.info.java",
            System.getProperty("java.vendor"),
            System.getProperty("java.version"),
            System.getProperty("java.vendor.url"))));

        sender.sendMessage(MessageUtil.message(locale.get(
            "cmd.info.jvm",
            System.getProperty("java.vm.vendor"),
            System.getProperty("java.vm.name"),
            System.getProperty("java.vm.version"))));

        sender.sendMessage(MessageUtil.message(locale.get(
            "cmd.info.processors",
            rt.availableProcessors()
        )));

        sender.sendMessage(MessageUtil.message(locale.get(
            "cmd.info.total_memory",
            Math.floor(rt.maxMemory() / 1024.0 / 1024.0)
        )));

        sender.sendMessage(MessageUtil.message(locale.get(
            "cmd.info.allocated_memory",
            Math.floor(rt.totalMemory() / 1024.0 / 1024.0)
        )));

        sender.sendMessage(MessageUtil.message(locale.get(
            "cmd.info.free_memory",
            Math.floor(rt.freeMemory() / 1024.0 / 1024.0)
        )));
    }
}
