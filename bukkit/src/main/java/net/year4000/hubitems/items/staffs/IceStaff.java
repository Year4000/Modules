package net.year4000.hubitems.items.staffs;

import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

@FunItemInfo(
    name = "icestaff.name",
    icon = Material.DIAMOND_HOE,
    description = "icestaff.description",
    permission = {"mu" , "icestaff.permission"},
    mana = 0.05F
)
public class IceStaff extends FunItem {
    @EventHandler
    public void use(PlayerInteractEvent event) {
        if (!isItem(event.getPlayer())) return;

        if (cost(event.getPlayer(), info.mana())) {
            event.getPlayer().sendMessage(info.description());
        }
    }
}
