package net.year4000.hubitems;

import com.ewized.utilities.bukkit.util.ItemUtil;
import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.core.module.ModuleInfo;
import net.year4000.hubitems.items.FunItemManager;
import net.year4000.hubitems.items.passive.Flight;
import net.year4000.hubitems.items.passive.NightVision;
import net.year4000.hubitems.items.passive.Speed;
import net.year4000.hubitems.items.staffs.FireBallStaff;
import net.year4000.hubitems.items.staffs.FireStaff;
import net.year4000.hubitems.items.staffs.IceStaff;
import net.year4000.hubitems.messages.Message;
import net.year4000.hubitems.messages.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ModuleInfo(
    name = "HubItems",
    version = "1.0",
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
    FireStaff.class,
    FireBallStaff.class
})
public class HubItems extends BukkitModule {
    private static Map<Locale, Map<Integer, ItemStack>> hotbar = new HashMap<Locale, Map<Integer, ItemStack>>() {{
        MessageManager.get().getLocales().forEach((l, p) -> {
            Message locale = new Message(l);

            // Game Servers
            put(l, new HashMap<Integer, ItemStack>() {{
                String title = locale.get("gameservers.name");
                String lore = locale.get("gameservers.click");
                put(0, ItemUtil.makeItem("enchanted_book", String.format(
                    "{'display':{'name':'%s', 'lore':['%s']}}",
                    title,
                    lore
                )));

                title = locale.get("hubs.name");
                lore = locale.get("hubs.click");
                put(8, ItemUtil.makeItem("enchanted_book", String.format(
                    "{'display':{'name':'%s', 'lore':['%s']}}",
                    title,
                    lore
                )));
            }});
        });
    }};

    @Override
    public void enable() {
        Bukkit.getScheduler().runTaskTimer(DuckTape.get(), new ManaClock(), 1, 1);
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
            event.setCancelled(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE));
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            Inventory inv = player.getInventory();

            inv.setContents(FunItemManager.get().loadItems(event.getPlayer()));

            hotbar.get(new Locale(player.getLocale())).forEach(inv::setItem);

            player.updateInventory();
        }
    }
}
