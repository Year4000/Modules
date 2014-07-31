package net.year4000.hubitems.items.passive;

import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

@FunItemInfo(
    name = "flight.name",
    icon = Material.ARROW,
    description = "flight.description",
    permission = {"pi", "flight.permission"},
    passive = true
)
public class Flight extends FunItem {
    @EventHandler
    public void onFlight(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setAllowFlight(player.hasPermission(info.permission()[0]));
    }
}
