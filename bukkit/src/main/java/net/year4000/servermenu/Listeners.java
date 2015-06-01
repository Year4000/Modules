package net.year4000.servermenu;

import net.year4000.servermenu.gui.AbstractGUI;
import net.year4000.servermenu.locales.Locales;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryClickedEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
}
