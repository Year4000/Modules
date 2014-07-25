package net.year4000.dressup.message;

import com.ewized.utilities.bukkit.util.BukkitLocale;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Message extends BukkitLocale {
    public Message(Player player) {
        super(player);
        localeManager = MessageManager.get();
    }

    public Message(Locale localea) {
        super(null);
        locale = localea.toString();
        localeManager = MessageManager.get();
    }
}
