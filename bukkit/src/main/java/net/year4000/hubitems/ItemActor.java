package net.year4000.hubitems;

import lombok.Data;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.items.FunItemManager;
import net.year4000.hubitems.utils.Common;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

@Data
public class ItemActor {
    /** Know Actors */
    private static Map<Player, ItemActor> playerActors = new WeakHashMap<>();

    private ItemActor(Player player) {
        this.player = player;
    }

    /** Get or create an instance of ItemActor */
    public static ItemActor get(Player player) {
        if (!playerActors.containsKey(player)) {
            ItemActor actor = new ItemActor(player);
            playerActors.put(player, actor);
            return actor;
        }

        return playerActors.get(player);
    }

    /** The player that this items exist */
    private Player player;

    /** The current item that the user has */
    private FunItemInfo currentItem;

    public void setCurrentItem(FunItemInfo currentItem) {
        this.currentItem = currentItem;
        applyFunItem();
    }

    /** Apply the current item to the player's inventory */
    public void applyFunItem() {
        if (currentItem == null) {
            HubItems.debug("Current Item is null cant set a null item");
            return;
        }

        applyFunItem(currentItem);
    }

    /** Apply the specific items to the player's inventory */
    public void applyFunItem(FunItemInfo funItem) {
        ItemStack item = FunItemManager.get().makeItem(player, funItem);

        ItemMeta meta = item.getItemMeta();

        // add right/left click to know what to do
        meta.setDisplayName(meta.getDisplayName() + FunItem.actionDisplay(player, funItem.action()));

        // only show mana price
        meta.setLore(Arrays.asList(meta.getLore().get(0)));

        // If item is bow make the item have infinity
        if (item.getType().equals(Material.BOW)) {
            //meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        }

        item.setItemMeta(meta);
        player.getInventory().setItem(4, item);
    }

    /*
        TODO Passive item state data
        This will let the player keep track of a
        passive item's state active or not active
        this will allow the user to chose a state for
        passive items.
     */
}
