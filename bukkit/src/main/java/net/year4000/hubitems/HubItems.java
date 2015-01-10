package net.year4000.hubitems;

import com.google.gson.Gson;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.items.FunItemManager;
import net.year4000.hubitems.items.bows.EggBow;
import net.year4000.hubitems.items.bows.EnderBow;
import net.year4000.hubitems.items.bows.TNTBow;
import net.year4000.hubitems.items.passive.Auras;
import net.year4000.hubitems.items.passive.Flight;
import net.year4000.hubitems.items.passive.NightVision;
import net.year4000.hubitems.items.passive.Speed;
import net.year4000.hubitems.items.shows.FireworkShow;
import net.year4000.hubitems.items.staffs.CorruptedStaff;
import net.year4000.hubitems.items.staffs.FireBallStaff;
import net.year4000.hubitems.items.staffs.IceStaff;
import net.year4000.hubitems.messages.Message;
import net.year4000.hubitems.messages.MessageManager;
import net.year4000.hubitems.utils.Common;
import net.year4000.hubitems.utils.Tracker;
import net.year4000.localewatchdog.PlayerChangeLocaleEvent;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.items.NBT;
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
    Auras.class,
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
    private static final Gson GSON = new Gson();
    private static final Map<Locale, Map<Integer, ItemStack>> HOT_BAR = new HashMap<Locale, Map<Integer, ItemStack>>() {{
        MessageManager.get().getLocales().forEach((l, p) -> {
            Message locale = new Message(l);

            // Game Servers
            put(l, new HashMap<Integer, ItemStack>() {{
                String title = locale.get("gameservers.name") + " &7(" + locale.get("action.right") + ")";
                String lore = locale.get("gameservers.description");
                put(0, makeServerMenuIcon(title, lore));

                //title = locale.get("classicservers.name") + " &7(" + locale.get("action.right") + ")";
                //lore = locale.get("classicservers.description");
                //put(1, makeServerMenuIcon(title, lore));

                title = locale.get("hubs.name") + " &7(" + locale.get("action.right") + ")";
                lore = locale.get("hubs.description");
                put(8, makeServerMenuIcon(title, lore));
            }});
        });
    }};

    private static ItemStack makeServerMenuIcon(String title, String lore) {
        NBT nbt = GSON.fromJson("{'display':{}}", NBT.class);
        nbt.getDisplay().setName(title);
        nbt.getDisplay().setLore(Common.loreDescription(lore));

        return ItemUtil.makeItem("enchanted_book", GSON.toJson(nbt));
    }

    @Override
    public void enable() {
        SchedulerUtil.repeatSync(new ManaClock(), (long) 0.1, TimeUnit.SECONDS);
        SchedulerUtil.repeatSync(new Tracker.TrackerRunner(), (long) 0.1, TimeUnit.SECONDS);
    }

    private static boolean mode(Player player) {
        return !player.getGameMode().equals(GameMode.CREATIVE) || !player.isOp();
    }

    private static Locale getLocaleOrDefault(Player player) {
        return new Locale(MessageManager.get().isLocale(player.getLocale()) ? player.getLocale() : Message.DEFAULT_LOCALE);
    }

    private static Locale getLocaleOrDefault(String code) {
        return new Locale(MessageManager.get().isLocale(code) ? code : Message.DEFAULT_LOCALE);
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

                    if (!FunItemManager.get().getItemMaterials().contains(item.getType())) {
                        inv.setContents(FunItemManager.get().loadItems(event.getPlayer()));
                        HOT_BAR.get(getLocaleOrDefault(player)).forEach(inv::setItem);
                        ItemActor.get(player).applyFunItem();
                    }
                    // Remove self then reset item contents
                    else {
                        ItemActor.get(player).setCurrentItem(null);
                        inv.setContents(FunItemManager.get().loadItems(event.getPlayer()));
                        HOT_BAR.get(getLocaleOrDefault(player)).forEach(inv::setItem);
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
                    HOT_BAR.get(getLocaleOrDefault(player)).forEach(inv::setItem);
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
            HOT_BAR.get(getLocaleOrDefault(player)).forEach(inv::setItem);
            ItemActor.get(player).applyFunItem();
            player.updateInventory();
        }

        @EventHandler
        public void onLocaleChange(PlayerChangeLocaleEvent event) {
            Player player = event.getPlayer();

            Inventory inv = player.getInventory();
            inv.setContents(FunItemManager.get().loadItems(event.getPlayer()));
            HOT_BAR.get(getLocaleOrDefault(event.getTo())).forEach(inv::setItem);

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

                    // set the items
                    ItemActor.get(player).setCurrentItem(info);

                    Inventory inv = player.getInventory();
                    inv.setContents(FunItemManager.get().loadItems(player));
                    HOT_BAR.get(getLocaleOrDefault(player)).forEach(inv::setItem);

                    // apply the items
                    ItemActor.get(player).applyFunItem(info);

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
