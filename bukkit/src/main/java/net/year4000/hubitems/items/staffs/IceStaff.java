package net.year4000.hubitems.items.staffs;

import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.utils.ParticleUtil;
import net.year4000.hubitems.utils.Tracker;
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
    //private List<Inventory> ice = new ArrayList<>();

    @EventHandler
    public void use(PlayerInteractEvent event) {
        if (!isItem(event.getPlayer()) || isRightClick(event.getAction())) return;

        if (cost(event.getPlayer(), info.mana())) {
            new Tracker(event.getPlayer().getWorld(), event.getPlayer().throwSnowball().getEntityId(), ParticleUtil.Particles.WATER_DRIP);
        }
    }
}
