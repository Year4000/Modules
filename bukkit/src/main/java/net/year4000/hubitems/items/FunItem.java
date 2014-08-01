package net.year4000.hubitems.items;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.hubitems.messages.Message;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

public abstract class FunItem implements Listener {
    protected final FunItemInfo info;

    public FunItem() {
        //System.out.println(this.getClass().getName());
        info = FunItemManager.get().loadItem(this.getClass());
    }

    /** Make the item cost mana */
    public boolean cost(Player player, float mana) {
        float cost = mana;
        float exp = player.getExp();

        if (exp - cost <= 0F && !player.getGameMode().equals(GameMode.CREATIVE)) {
            String itemName = player.getItemInHand().getItemMeta().getDisplayName();
            double need = Math.abs(cost - exp);
            String message = new Message(player).get("mana.required", need, itemName);
            player.sendMessage(" " + message);
            FunEffectsUtil.playSound(player, Sound.BLAZE_HIT);
            return false;
        }

        player.setExp(exp - cost);
        return true;
    }

    public boolean isItem(Player player) {
        try {
            return MessageUtil.stripColors(player.getItemInHand().getItemMeta().getDisplayName()).equals(MessageUtil.stripColors(new Message(player).get(info.name())));
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean isLeftClick(Action action) {
        return action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK);
    }

    public boolean isRightClick(Action action) {
        return action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK);
    }
}
