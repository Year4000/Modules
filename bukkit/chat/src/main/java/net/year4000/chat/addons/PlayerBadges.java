package net.year4000.chat.addons;

import net.year4000.chat.events.MessageSentEvent;
import net.year4000.chat.formatter.FormatterManager;
import net.year4000.chat.Message;
import net.year4000.utilities.bukkit.BadgeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerBadges implements Listener {
    private static final String BADGE = "badge";
    private BadgeManager manager = new BadgeManager();

    public PlayerBadges() {
        FormatterManager.get().addFormatter(BADGE, m -> m.getMeta(BADGE));
    }

    @EventHandler
    public void onSent(MessageSentEvent event) {
        Message message = event.getMessage();
        Player player = Bukkit.getPlayer(message.getActorUUID());
        message.setMeta(BADGE, manager.getBadge(player));
    }
}
