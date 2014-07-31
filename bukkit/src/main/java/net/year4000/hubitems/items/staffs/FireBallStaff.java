package net.year4000.hubitems.items.staffs;

import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

@FunItemInfo(
    name = "fireballstaff.name",
    icon = Material.STONE_HOE,
    description = "fireballstaff.description",
    permission = {"mu" , "fireballstaff.permission"},
    mana = 0.10F
)
public class FireBallStaff extends FunItem {
    @EventHandler
    public void use(PlayerInteractEvent event) {
        if (!isItem(event.getPlayer())) return;

        if (cost(event.getPlayer(), info.mana())) {
            event.getPlayer().sendMessage(info.description());
        }
    }
}
