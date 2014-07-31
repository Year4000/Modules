package net.year4000.hubitems;

import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.core.module.ModuleInfo;
import net.year4000.hubitems.items.FunItemManager;
import net.year4000.hubitems.items.staffs.FireBallStaff;
import net.year4000.hubitems.items.staffs.FireStaff;
import net.year4000.hubitems.items.staffs.IceStaff;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@ModuleInfo(
    name = "HubItems",
    version = "1.0",
    description = "Control the items that the players can use including fun items.",
    authors = {"Year4000"}
)
@ModuleListeners({
    // Self
    HubItems.HubListener.class,
    // Items
    IceStaff.class,
    FireStaff.class,
    FireBallStaff.class
})
public class HubItems extends BukkitModule {
    @Override
    public void enable() {
        Bukkit.getScheduler().runTaskTimer(DuckTape.get(), new ManaClock(), 1, 1);
    }

    public static class HubListener implements Listener {
        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (ManaClock.getIsReady().get(event.getPlayer()) == null) {
                ManaClock.getIsReady().put(event.getPlayer(), true);
            }
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            event.getPlayer().getInventory().setContents(FunItemManager.get().loadItems(event.getPlayer()));
            event.getPlayer().updateInventory();
        }
    }
}
