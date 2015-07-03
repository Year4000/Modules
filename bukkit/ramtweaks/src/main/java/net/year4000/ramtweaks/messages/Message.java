package net.year4000.ramtweaks.messages;

import net.year4000.utilities.bukkit.BukkitLocale;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message extends BukkitLocale {
    public Message(CommandSender sender) {
        super(sender instanceof Player ? (Player) sender : null);
        localeManager = MessageManager.get();
    }
}
