package net.year4000.servermenu;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import com.ewized.utilities.bukkit.util.ItemUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.servermenu.menus.MenuManager;
import net.year4000.servermenu.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuListener implements Listener {
    /** Open the menu by hotbar */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            Message locale = new Message(player);
            boolean rightBlock = event.getAction() == Action.RIGHT_CLICK_BLOCK;
            boolean rightAir = event.getAction() == Action.RIGHT_CLICK_AIR;
            String item =  MessageUtil.stripColors(player.getItemInHand().getItemMeta().getDisplayName());

            if (MenuManager.get().isMenu(item) && (rightBlock || rightAir)) {
                player.sendMessage(MessageUtil.replaceColors(locale.get("menu.open", player.getItemInHand().getItemMeta().getDisplayName())));
                Bukkit.getScheduler().runTaskAsynchronously(DuckTape.get(), () -> MenuManager.get().openMenu(player, item));
                event.setCancelled(true);
            }
        } catch (NullPointerException e) {
            // item is not proper
            //e.printStackTrace();
        }
    }

    /** Open a menu by item */
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Message locale = new Message(player);

        try {
            ItemMeta i = event.getCurrentItem().getItemMeta();
            String nameStriped = MessageUtil.stripColors(i.getDisplayName());

            if (event.getInventory().contains(ItemUtil.makeItem("redstone_block", "{'display':{'name':'" + new Message(player).get("menu.close") + "'}}"))) {
                event.setCancelled(true);
            }

            if (i.getEnchants().size() == 0 && i.getLore().contains(MessageUtil.replaceColors(locale.get("menu.click", nameStriped)))) {
                FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
                player.sendMessage(MessageUtil.replaceColors(locale.get("menu.open", i.getDisplayName())));
                Bukkit.getScheduler().runTaskAsynchronously(DuckTape.get(), () -> MenuManager.get().openMenu(player, nameStriped));
            }
        } catch (NullPointerException e) {
            // Left Blank as this will happen is its not a good item
            //e.printStackTrace();
        }
    }

    /** Connect to a server */
    @EventHandler
    public void onServerClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Message locale = new Message(player);

        try {
            ItemMeta i = event.getCurrentItem().getItemMeta();

            if (i.getLore().contains(MessageUtil.replaceColors(locale.get("server.click")))) {
                FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
                player.sendMessage(MessageUtil.replaceColors(locale.get("server.connect", i.getDisplayName())));
                new BungeeSender(MessageUtil.stripColors(i.getDisplayName())).send(player);
                player.closeInventory();
            }

            // cancel the event
            if (event.getInventory().contains(ItemUtil.makeItem("redstone_block", "{'display':{'name':'" + new Message(player).get("menu.close") + "'}}"))) {
                event.setCancelled(true);
            }

        } catch (NullPointerException e) {
            // Left Blank as this will happen if its not a good item
            //e.printStackTrace();
        }
    }

    /** Close the menu */
    @EventHandler
    public void onCloseMenu(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        try {
            String item = event.getCurrentItem().getItemMeta().getDisplayName();
            String closeItem = new Message(player).get("menu.close");

            if (MessageUtil.stripColors(item).equalsIgnoreCase(MessageUtil.stripColors(closeItem))) {
                FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
                player.closeInventory();
                event.setCancelled(true);
            }
        } catch (Exception e) {
            // only if item is not right
            //e.printStackTrace();
        }
    }
}
