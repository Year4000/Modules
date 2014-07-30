package net.year4000.servermenu.message;

import com.ewized.utilities.bukkit.util.BukkitLocale;
import org.bukkit.entity.Player;

public class Message extends BukkitLocale {
    public Message(Player player) {
        super(player);
        localeManager = MessageManager.get();
    }

    public Message(String locale) {
        super(null);
        this.locale = locale;
        localeManager = MessageManager.get();
    }
}
