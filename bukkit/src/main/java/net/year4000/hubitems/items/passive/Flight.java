package net.year4000.hubitems.items.passive;

import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.items.PassiveState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.TimeUnit;

@FunItemInfo(
    name = "flight.name",
    icon = Material.ARROW,
    description = "flight.description",
    permission = {"pi", "sigma", "phi", "delta", "omega", "flight.permission"},
    passive = PassiveState.ALLWAYS_ON
)
public class Flight extends FunItem {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFlight(PlayerJoinEvent event) {
        SchedulerUtil.runAsync(() -> {
            Player player = event.getPlayer();

            try {
                player.setAllowFlight(player.hasPermission(info.permission()[0]));
            } catch (Exception e) {
                player.kickPlayer(e.getMessage());
            }
        }, 1, TimeUnit.SECONDS);
    }
}
