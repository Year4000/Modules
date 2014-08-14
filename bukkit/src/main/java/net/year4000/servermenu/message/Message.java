package net.year4000.servermenu.message;

import net.year4000.utilities.bukkit.BukkitLocale;
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
