package net.year4000.hubitems.items.staffs;

import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

@FunItemInfo(
    name = "firestaff.name",
    icon = Material.GOLD_HOE,
    description = "firestaff.description",
    permission = {"mu" , "firestaff.permission"},
    mana = 0.15F
)
public class FireStaff extends FunItem {
    @EventHandler
    public void use(PlayerInteractEvent event) {
        if (!isItem(event.getPlayer())) return;

        if (cost(event.getPlayer(), info.mana())) {
            event.getPlayer().sendMessage(info.description());
        }
    }
}
