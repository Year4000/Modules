package net.year4000.ramtweaks;

import com.google.common.base.Ascii;
import net.year4000.ramtweaks.messages.Message;
import net.year4000.ramtweaks.messages.ShutdownMessage;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.commands.Command;
import net.year4000.utilities.bukkit.commands.CommandContext;
import net.year4000.utilities.bukkit.commands.CommandException;
import net.year4000.utilities.bukkit.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;

public final class RamCommands {
    @Command(
        aliases = {"restart"},
        desc = "Restart the server with a friendly 10 sec delay.",
        flags = "t:",
        max = 1
    )
    @CommandPermissions({"eramtweaks.restart"})
    public static void restart(CommandContext args, CommandSender sender) throws CommandException {
        new ShutdownMessage(args.hasFlag('t') ? args.getFlagInteger('t') : 10);
    }

    @Command(
        aliases = {"uptime"},
        desc = "Get the uptime of the server."
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
        aliases = {"info", "serverinfo", "sinfo"},
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

    @Command(
        aliases = {"unload"},
        desc = "Unloads all loaded chunks."
    )
    @CommandPermissions({"eramtweaks.unload"})
    public static void unloadChunks(CommandContext args, CommandSender sender) throws CommandException {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(MessageUtil.message(new Message(player).get("unload.notice")));
        }

        int totalChunks = 0;
        int totalWorlds = Bukkit.getWorlds().size();

        for (World w : Bukkit.getWorlds()) {
            totalChunks += w.getLoadedChunks().length;

            for (Chunk c : w.getLoadedChunks()) {
                c.unload(true, true);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(MessageUtil.message(new Message(player).get("unload.done")));
        }
        sender.sendMessage(MessageUtil.message(new Message(sender).get("unload.stats", totalChunks, totalWorlds)));
    }

    @Command(
        aliases = {"chunks", "mem"},
        desc = "Get chunk data on each loaded world."
    )
    @CommandPermissions({"eramtweaks.chunks"})
    public static void loadedChunks(CommandContext args, CommandSender sender) throws CommandException {
        Message locale = new Message(sender);
        int totalChunks = 0;
        int totalEnties = 0;
        int totalTitleEnties = 0;

        for (World w : Bukkit.getWorlds()) {
            int worldChunks = w.getLoadedChunks().length;
            int worldEnties = 0;
            int worldTitleEnties = 0;

            for (Chunk c : w.getLoadedChunks()) {
                worldEnties += c.getEntities().length;
                worldTitleEnties += c.getTileEntities().length;
            }

            totalEnties += worldEnties;
            totalTitleEnties += worldTitleEnties;
            totalChunks += worldChunks;

            sender.sendMessage(MessageUtil.message(locale.get(
                "mem.world",
                w.getEnvironment().name(),
                Ascii.truncate(w.getName(), 16, "..."),
                worldChunks,
                worldEnties,
                worldTitleEnties
            )));
        }

        sender.sendMessage(MessageUtil.message(locale.get("mem.total", totalChunks, totalEnties, totalTitleEnties)));
    }
}
