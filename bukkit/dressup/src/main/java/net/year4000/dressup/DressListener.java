package net.year4000.dressup;

import com.google.common.collect.ImmutableSet;
import net.year4000.dressup.message.Message;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class DressListener implements Listener {
    private Map<Player, String> lastOpened = new WeakHashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event) {
        Player player = ((Player)event.getWhoClicked());
        Message locale = new Message(player);
        Set<ItemStack> playerItems = new HashSet<>(Arrays.asList(player.getInventory().getContents()));
        //System.out.println("Slot: " + event.getSlot());
        //System.out.println("Raw Slot: " + event.getRawSlot());

        if ((event.getSlot() > 35 && event.getSlot() < 40) || player.getGameMode() != GameMode.ADVENTURE) {
            event.setCancelled(true);
        }

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
            if (isVIP(player)) {
                // Hat
                if (lastOpened.get(player).equals(Settings.HAT)) {
                    player.sendMessage(locale.get("set.hat"));
                    ItemStack current = event.getCurrentItem().clone();
                    ItemMeta meta = current.getItemMeta();
                    meta.setLore(null);
                    current.setItemMeta(meta);
                    player.getInventory().setHelmet(current);
                }
                // Chest
                if (lastOpened.get(player).equals(Settings.CHEST)) {
                    player.sendMessage(locale.get("set.shirt"));
                    ItemStack current = event.getCurrentItem().clone();
                    ItemMeta meta = current.getItemMeta();
                    meta.setLore(null);
                    current.setItemMeta(meta);
                    player.getInventory().setChestplate(current);
                }
                // Pants
                if (lastOpened.get(player).equals(Settings.PANTS)) {
                    player.sendMessage(locale.get("set.pants"));
                    ItemStack current = event.getCurrentItem().clone();
                    ItemMeta meta = current.getItemMeta();
                    meta.setLore(null);
                    current.setItemMeta(meta);
                    player.getInventory().setLeggings(current);
                }
                // Boots
                if (lastOpened.get(player).equals(Settings.BOOTS)) {
                    player.sendMessage(locale.get("set.boots"));
                    ItemStack current = event.getCurrentItem().clone();
                    ItemMeta meta = current.getItemMeta();
                    meta.setLore(null);
                    current.setItemMeta(meta);
                    player.getInventory().setBoots(current);
                }

                // Let the user know something happened
                FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
                //noinspection deprecation
                player.updateInventory();
                player.closeInventory();
                lastOpened.remove(player);
            }
            else {
                player.sendMessage(MessageUtil.replaceColors(locale.get("access.vip") + " \n &awww.year4000.net/page/shop"));
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            lastOpened.remove(event.getPlayer());
        }
    }

    private static Set<String> VIPS = ImmutableSet.of("theta", "mu", "pi", "sigma", "phi", "delta", "omega");

    /** Is the selected player a VIP */
    public static boolean isVIP(Player player) {
        for (String permission : VIPS) {
            if (player.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }
}
