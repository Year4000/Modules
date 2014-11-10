package net.year4000.servermenu;

import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.servermenu.menus.MenuManager;
import net.year4000.servermenu.message.MenuMessage;
import net.year4000.servermenu.message.Message;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MenuListener implements Listener {
    private Map<Player, BukkitTask> pendingMenu = new ConcurrentHashMap<>();

    /** Open the menu by an entity */
    @EventHandler
    public void onEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();

        if (entity instanceof LivingEntity) {
            Player player = event.getPlayer();
            LivingEntity livingEntity = (LivingEntity) entity;

            try {
                System.out.println(livingEntity.getCustomName());
                Message locale = new Message(player);
                String name = MessageUtil.stripColors(livingEntity.getCustomName());

                if (MenuManager.get().isMenu(player, name)) {
                    // pending task cancel it and start new one
                    if (pendingMenu.keySet().contains(player)) {
                        pendingMenu.remove(player).cancel();
                    }

                    player.sendMessage(locale.get("menu.open", name));
                    generateOpenMenuTask(MenuManager.Type.RAW_MENU, player, name);

                    event.setCancelled(true);
                }
            } catch (NullPointerException e) {
                // item is not proper
                //e.printStackTrace();
            }
        }
    }

    /** Open the menu by hotbar */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            Message locale = new Message(player);
            boolean rightBlock = event.getAction() == Action.RIGHT_CLICK_BLOCK;
            boolean rightAir = event.getAction() == Action.RIGHT_CLICK_AIR;
            String item = MessageUtil.stripColors(player.getItemInHand().getItemMeta().getDisplayName())
                .replace(" (" + new MenuMessage(player.getLocale()).get("action.right") + ")", "");

            if (MenuManager.get().isMenu(player, item) && (rightBlock || rightAir)) {
                // pending task cancel it and start new one
                if (pendingMenu.keySet().contains(player)) {
                    pendingMenu.remove(player).cancel();
                }

                player.sendMessage(locale.get("menu.open", item));
                generateOpenMenuTask(MenuManager.Type.NORMAL, player, item);

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

            if (event.getInventory().contains(ServerMenu.closeButton(player))) {
                event.setCancelled(true);
            }

            if (i.getEnchants().size() == 0 && i.getLore().contains(locale.get("menu.click"))) {
                // pending task cancel it and start new one
                if (pendingMenu.keySet().contains(player)) {
                    pendingMenu.remove(player).cancel();
                }

                FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
                player.sendMessage(locale.get("menu.open", i.getDisplayName()).replaceAll(ChatColor.COLOR_CHAR + "l", ""));
                generateOpenMenuTask(MenuManager.Type.RAW_MENU, player, nameStriped);
            }
        } catch (NullPointerException e) {
            // Left Blank as this will happen is its not a good item
            //e.printStackTrace();
        }
    }

    /** Generate the task to open the menu */
    private void generateOpenMenuTask(MenuManager.Type type, Player player, String item) {
        pendingMenu.put(player, SchedulerUtil.runAsync(() -> {
            MenuManager.get().openMenu(type, player, item);
            pendingMenu.remove(player);
        }));
    }

    /** Connect to a server */
    @EventHandler
    public void onServerClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Message locale = new Message(player);

        try {
            ItemMeta i = event.getCurrentItem().getItemMeta();

            if (i.getLore().contains(locale.get("server.click"))) {
                FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
                player.sendMessage(locale.get("server.connect", i.getDisplayName()).replaceAll(ChatColor.COLOR_CHAR + "l", ""));
                new BungeeSender(MessageUtil.stripColors(i.getDisplayName())).send(player);
                player.closeInventory();
            }

            // cancel the event
            if (event.getInventory().contains(ServerMenu.closeButton(player))) {
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
        } catch (NullPointerException e) {
            // only if item is not right
            //e.printStackTrace();
        }
    }
}
