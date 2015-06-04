package net.year4000.servermenu;

import net.year4000.servermenu.gui.AbstractGUI;
import net.year4000.servermenu.locales.HubMessageFactory;
import net.year4000.servermenu.locales.Locales;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.Locale;

public class Listeners implements Listener {

    @EventHandler
    public void onIconClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        String title = inventory.getTitle();
        Locale locale;
        Player player;

        // If a player click inventory get their locale else return
        if (event.getWhoClicked() instanceof Player) {
            player = (Player) event.getWhoClicked();
            locale = new Locale(player.getLocale());
        }
        else {
            return;
        }

        // Get proper locale
        for (AbstractGUI gui : ServerMenu.inst.getMenus()) {
            Inventory guiInventory = gui.getInventory(locale);

            if (guiInventory.getTitle().equals(title)) {
                int slot = event.getSlot();
                int rows = slot / InventoryGUI.COLS;
                int cols = slot % InventoryGUI.COLS;

                gui.processAction(player, rows, cols);
                event.setCancelled(true);
            }
        }
    }

    /** Open the menu by hot bar */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            HubMessageFactory.Message locale = new HubMessageFactory.Message(player);
            boolean rightBlock = event.getAction() == Action.RIGHT_CLICK_BLOCK;
            boolean rightAir = event.getAction() == Action.RIGHT_CLICK_AIR;
            String item = MessageUtil.stripColors(player.getItemInHand().getItemMeta().getDisplayName())
                .replace(" (" + locale.get("action.right") + ")", "");
            String itemMatch = MessageUtil.stripColors(locale.get("gameservers.name"));

            //player.sendMessage(item + ":" + itemMatch);

            if (item.equals(itemMatch) && (rightBlock || rightAir)) {
                player.sendMessage(Locales.MENU_OPEN.translate(player, "Main Menu"));
                ServerMenu.inst.getMenus().get(0).openInventory(player);
                event.setCancelled(true);
            }
        } catch (NullPointerException e) {
            // item is not proper
            // e.printStackTrace();
        }
    }
}
