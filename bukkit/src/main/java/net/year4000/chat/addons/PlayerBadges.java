package net.year4000.chat.addons;

import net.year4000.bages.BadgeManager;
import net.year4000.chat.events.MessageSentEvent;
import net.year4000.chat.formatter.FormatterManager;
import net.year4000.chat.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerBadges implements Listener {
    BadgeManager manager = new BadgeManager();

    public PlayerBadges() {
        FormatterManager.get().addFormatter("badge", m -> m.getMeta("badge"));
    }

    @EventHandler
    public void onSent(MessageSentEvent event) {
        Message message = event.getMessage();
        Player player = Bukkit.getPlayer(message.getActor().getName());
        message.setMeta("badge", manager.getBadge(player));
    }
}
