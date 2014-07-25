package net.year4000.dressup;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import com.ewized.utilities.bukkit.util.ItemUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.dressup.message.Message;
import net.year4000.ducktape.bukkit.DuckTape;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class DressListener implements Listener {
    private Map<Player, String> lastOpened = new WeakHashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = ((Player)event.getWhoClicked());
        Message locale = new Message(player);
        List<ItemStack> playerItems = Arrays.asList(player.getInventory().getContents());
        //System.out.println("Slot: " + event.getSlot());
        //System.out.println("Raw Slot: " + event.getRawSlot());

        event.setCancelled(event.getSlot() > 35 && event.getSlot() < 40);

        // Hat 39
        if (event.getSlot() == 39) {
            player.getInventory().setBoots(ItemUtil.makeItem("air"));
            player.openInventory(DressUp.openHat(player));
            lastOpened.put(player, Settings.HAT);
            return;
        }
        // Chest 38
        else if (event.getSlot() == 38) {
            player.getInventory().setBoots(ItemUtil.makeItem("air"));
            player.openInventory(DressUp.openChest(player));
            lastOpened.put(player, Settings.CHEST);
            return;
        }
        // Pants 37
        else if (event.getSlot() == 37) {
            player.getInventory().setBoots(ItemUtil.makeItem("air"));
            player.openInventory(DressUp.openPants(player));
            lastOpened.put(player, Settings.PANTS);
            return;
        }
        // Boots 36
        else if (event.getSlot() == 36) {
            player.getInventory().setBoots(ItemUtil.makeItem("air"));
            player.openInventory(DressUp.openBoots(player));
            lastOpened.put(player, Settings.BOOTS);
            return;
        }

        if (lastOpened.get(player) != null && !playerItems.contains(event.getCurrentItem())) {
            if (player.hasPermission("theta")) {
                Bukkit.getScheduler().runTask(DuckTape.get(), () -> {
                    // Hat
                    if (lastOpened.get(player).equals(Settings.HAT)) {
                        player.sendMessage(MessageUtil.replaceColors(locale.get("set.hat")));
                        player.getInventory().setHelmet(event.getCurrentItem());
                    }
                    // Chest
                    if (lastOpened.get(player).equals(Settings.CHEST)) {
                        player.sendMessage(MessageUtil.replaceColors(locale.get("set.shirt")));
                        player.getInventory().setChestplate(event.getCurrentItem());
                    }
                    // Pants
                    if (lastOpened.get(player).equals(Settings.PANTS)) {
                        player.sendMessage(MessageUtil.replaceColors(locale.get("set.pants")));
                        player.getInventory().setLeggings(event.getCurrentItem());
                    }
                    // Boots
                    if (lastOpened.get(player).equals(Settings.BOOTS)) {
                        player.sendMessage(MessageUtil.replaceColors(locale.get("set.boots")));
                        player.getInventory().setBoots(event.getCurrentItem());
                    }

                    // Let the user know something happened
                    FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
                    //noinspection deprecation
                    player.updateInventory();
                    player.closeInventory();
                    lastOpened.remove(player);
                });
            }
            else {
                player.sendMessage(MessageUtil.replaceColors(locale.get("access.vip") + " \n &awww.year4000.net/page/shop"));
            }

            event.setCancelled(true);
        }
    }
}
