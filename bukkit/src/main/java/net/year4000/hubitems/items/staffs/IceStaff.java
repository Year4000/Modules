package net.year4000.hubitems.items.staffs;

import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.utils.Common;
import net.year4000.hubitems.utils.ParticleUtil;
import org.bukkit.Location;
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
            //event.getPlayer().sendMessage(info.description());

            Location player = event.getPlayer().getLocation();
            Location looking = Common.getLooking(event.getPlayer());

            Common.getLines(player, looking, 100).forEach(loc -> ParticleUtil.sendPacket(event.getPlayer(), ParticleUtil.Particles.WATER_DRIP, loc));
            event.getPlayer().throwSnowball();
            event.getPlayer().throwSnowball();
        }
    }
}
