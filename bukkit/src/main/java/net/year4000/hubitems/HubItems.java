package net.year4000.hubitems;

import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.items.FunItemManager;
import net.year4000.hubitems.items.bows.EggBow;
import net.year4000.hubitems.items.bows.EnderBow;
import net.year4000.hubitems.items.bows.TNTBow;
import net.year4000.hubitems.items.passive.Flight;
import net.year4000.hubitems.items.passive.NightVision;
import net.year4000.hubitems.items.passive.Speed;
import net.year4000.hubitems.items.shows.FireworkShow;
import net.year4000.hubitems.items.staffs.CorruptedStaff;
import net.year4000.hubitems.items.staffs.FireBallStaff;
import net.year4000.hubitems.items.staffs.IceStaff;
import net.year4000.hubitems.messages.Message;
import net.year4000.hubitems.messages.MessageManager;
import net.year4000.hubitems.utils.Tracker;
import net.year4000.localewatchdog.PlayerChangeLocaleEvent;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "HubItems",
    version = "1.1",
    description = "Control the items that the players can use including fun items.",
    authors = {"Year4000"}
)
@ModuleListeners({
    // Self
    HubItems.HubListener.class,
    // Ability
    NightVision.class,
    Speed.class,
    Flight.class,
    // Items
    IceStaff.class,
    FireBallStaff.class,
    CorruptedStaff.class,
    EnderBow.class,
    EggBow.class,
    TNTBow.class,
    FireworkShow.class
})
public class HubItems extends BukkitModule {
    private static final Map<Locale, Map<Integer, ItemStack>> HOT_BAR = new HashMap<Locale, Map<Integer, ItemStack>>() {{
        MessageManager.get().getLocales().forEach((l, p) -> {
            Message locale = new Message(l);

            // Game Servers
            put(l, new HashMap<Integer, ItemStack>() {{
                String title = locale.get("gameservers.name");
                String lore = locale.get("gameservers.click");
                put(0, ItemUtil.makeItem("enchanted_book", String.format(
                    "{'display':{'name':'%s', 'lore':['','%s']}}",
                    title,
                    lore
                )));

                title = locale.get("hubs.name");
                lore = locale.get("hubs.click");
                put(8, ItemUtil.makeItem("enchanted_book", String.format(
                    "{'display':{'name':'%s', 'lore':['','%s']}}",
                    title,
                    lore
                )));
            }});
        });
    }};

    @Override
    public void enable() {
        SchedulerUtil.repeatSync(new ManaClock(), (long) 0.1, TimeUnit.SECONDS);
        SchedulerUtil.repeatSync(new Tracker.TrackerRunner(), (long) 0.1, TimeUnit.SECONDS);
    }

    public static boolean mode(Player player) {
        return !player.getGameMode().equals(GameMode.CREATIVE) || !player.isOp();
    }

    /** The listeners that control this module */
    public static class HubListener implements Listener {
        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (ManaClock.getIsReady().get(event.getPlayer()) == null) {
                ManaClock.getIsReady().put(event.getPlayer(), true);
            }
        }

        @EventHandler
        public void onDrop(PlayerDropItemEvent event) {
            final Player player = event.getPlayer();
            final ItemStack item = event.getItemDrop().getItemStack();

            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                SchedulerUtil.runSync(() -> {
                    Inventory inv = player.getInventory();
                    inv.setContents(FunItemManager.get().loadItems(event.getPlayer()));
                    HOT_BAR.get(new Locale(player.getLocale())).forEach(inv::setItem);

                    if (!FunItemManager.get().getItemMaterials().contains(item.getType())) {
                        ItemActor.get(player).applyFunItem();
                    }
                    else {
                        ItemActor.get(player).setCurrentItem(null);
                    }

                    player.updateInventory();
                });

                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onJoin(PlayerJoinEvent event) {
            SchedulerUtil.runSync(() -> {
                Player player = event.getPlayer();

                try {
                    Inventory inv = player.getInventory();
                    inv.setContents(FunItemManager.get().loadItems(event.getPlayer()));
                    Locale locale = new Locale(MessageManager.get().isLocale(player.getLocale()) ? player.getLocale() : Message.DEFAULT_LOCALE);
                    HOT_BAR.get(locale).forEach(inv::setItem);
                    player.updateInventory();
                } catch (Exception e) {
                    player.kickPlayer(e.getMessage());
                }
            }, 1, TimeUnit.SECONDS);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onJoin(PlayerRespawnEvent event) {
            Player player = event.getPlayer();

            Inventory inv = player.getInventory();
            inv.setContents(FunItemManager.get().loadItems(event.getPlayer()));
            HOT_BAR.get(new Locale(player.getLocale())).forEach(inv::setItem);
            ItemActor.get(player).applyFunItem();
            player.updateInventory();
        }

        @EventHandler
        public void onLocaleChange(PlayerChangeLocaleEvent event) {
            Player player = event.getPlayer();

            Inventory inv = player.getInventory();
            inv.setContents(FunItemManager.get().loadItems(event.getPlayer()));
            HOT_BAR.get(new Locale(event.getTo())).forEach(inv::setItem);

            ItemActor.get(player).applyFunItem();
            player.updateInventory();
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onClicked(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player)) return;

            Player player = (Player) event.getWhoClicked();
            Message locale = new Message(player);

            try {
                ItemMeta i = event.getCurrentItem().getItemMeta();
                String nameStriped = MessageUtil.stripColors(i.getDisplayName());

                if (i.getLore().contains(locale.get("mana.select"))) {
                    FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
                    FunItemInfo info = FunItemManager.get().getItemInfo(player, nameStriped);

                    ItemActor.get(player).setCurrentItem(info);

                    player.updateInventory();
                    player.closeInventory();
                }
            } catch (NullPointerException e) {
                // Left Blank as this will happen is its not a good item
                //e.printStackTrace();
            }

            if (Arrays.asList(player.getInventory().getContents()).contains(event.getCurrentItem()) && mode(player)) {
                event.setCancelled(true);
            }
        }
    }
}
