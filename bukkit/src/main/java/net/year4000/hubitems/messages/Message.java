package net.year4000.hubitems.messages;

import com.ewized.utilities.bukkit.util.BukkitLocale;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Message extends BukkitLocale {
    public Message(Player player) {
        super(player);
        localeManager = MessageManager.get();
    }

    public Message(Locale locale) {
        super(null);
        this.locale = locale.toString();
        localeManager = MessageManager.get();
    }
}
