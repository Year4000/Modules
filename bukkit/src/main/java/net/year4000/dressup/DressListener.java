package net.year4000.dressup;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.dressup.message.Message;
import net.year4000.ducktape.bukkit.DuckTape;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DressListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = ((Player)event.getWhoClicked());
        Message locale = new Message(player);
        //System.out.println("Slot" + event.getSlot());
        //System.out.println("Raw Slot" + event.getRawSlot());

        if (event.getInventory().getName().contains(locale.get("keyword.title"))) {
            if (player.hasPermission("theta")) {
                Bukkit.getScheduler().runTask(DuckTape.get(), () -> {
                    // Hat
                    if (event.getInventory().getName().contains("keyword.hat")) {
                        player.sendMessage(MessageUtil.replaceColors(locale.get("set.hat")));
                        player.getInventory().setHelmet(event.getCurrentItem().clone());
                    }
                    // Chest
                    else if (event.getInventory().getName().contains("keyword.shirt")) {
                        player.sendMessage(MessageUtil.replaceColors(locale.get("set.shirt")));
                        player.getInventory().setChestplate(event.getCurrentItem().clone());
                    }
                    // Pants
                    else if (event.getInventory().getName().contains("keyword.pants")) {
                        player.sendMessage(MessageUtil.replaceColors(locale.get("set.pants")));
                        player.getInventory().setLeggings(event.getCurrentItem().clone());
                    }
                    // Boots
                    else if (event.getInventory().getName().contains("keyword.boots")) {
                        player.sendMessage(MessageUtil.replaceColors(locale.get("set.boots")));
                        player.getInventory().setBoots(event.getCurrentItem().clone());
                    }

                    // Let the user know something happened
                    FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
                    //noinspection deprecation
                    player.updateInventory();
                    player.closeInventory();
                });
            }
            else {
                player.sendMessage(MessageUtil.replaceColors(locale.get("access.vip") + " \n &awww.year4000.net/page/shop"));
            }

            event.setCancelled(true);
            return;
        }

        event.setCancelled(event.getSlot() > 35);

        // Hat 39
        if (event.getSlot() == 39)
            event.getWhoClicked().openInventory(DressUp.openHat(player));
            // Chest 38
        else if (event.getSlot() == 38)
            event.getWhoClicked().openInventory(DressUp.openChest(player));
            // Pants 37
        else if (event.getSlot() == 37)
            event.getWhoClicked().openInventory(DressUp.openPants(player));
            // Boots 36
        else if (event.getSlot() == 36)
            event.getWhoClicked().openInventory(DressUp.openBoots(player));
    }
}
