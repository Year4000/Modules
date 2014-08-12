package net.year4000.hubitems.items;

import net.year4000.hubitems.ItemActor;
import net.year4000.hubitems.messages.Message;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class FunItem implements Listener {
    protected final FunItemInfo info;

    public FunItem() {
        //System.out.println(this.getClass().getName());
        info = FunItemManager.get().loadItem(this.getClass());
    }

    /** Make the item cost mana */
    public boolean cost(Player player, float mana) {
        float exp = player.getExp();

        if (exp - mana <= 0F && !player.getGameMode().equals(GameMode.CREATIVE)) {
            Message locale = new Message(player);
            String itemName = "&a" + locale.get(ItemActor.get(player).getCurrentItem().name());
            // DO NOT TOUCH THIS CONVERSION FORMAL IT WORKS
            double need = (((mana - exp + mana) * 0.10F) * 1000) - (mana * 0.10F * 1000);
            String message = locale.get("mana.required", need, itemName);
            player.sendMessage(" " + message.replaceAll(ChatColor.COLOR_CHAR + "l", ""));
            FunEffectsUtil.playSound(player, Sound.BLAZE_HIT);
            return false;
        }

        player.setExp(exp - mana);
        return true;
    }

    public boolean isItem(org.bukkit.event.block.Action action, Player player) {
        try {
            //HubItems.debug("Item Action: " + action.name() + " | " + info.action().name());
            if (!info.action().isCorrectAction(action)) return false;

            String item = MessageUtil.stripColors(new Message(player).get(info.name()) + actionDisplay(player, info.action()));
            return MessageUtil.stripColors(player.getItemInHand().getItemMeta().getDisplayName()).equals(item);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static String actionDisplay(Player player, Action action) {
        Message locale = new Message(player);
        if (action == Action.LEFT) {
            return MessageUtil.replaceColors(" &8&l(" + locale.get("action.left") + ")");
        }
        else if (action == Action.RIGHT) {
            return MessageUtil.replaceColors(" &8&l(" + locale.get("action.right") + ")");
        }
        else {
            throw new UnsupportedOperationException(action.name() + " is not a valid action.");
        }
    }
}
