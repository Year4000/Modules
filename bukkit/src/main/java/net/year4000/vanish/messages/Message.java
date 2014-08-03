package net.year4000.vanish.messages;

import com.ewized.utilities.bukkit.util.BukkitLocale;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message extends BukkitLocale {
    public Message(CommandSender sender) {
        super(sender instanceof Player ? (Player) sender : null);
        localeManager = MessageManager.get();
    }
}
