package net.year4000.hubitems.items.staffs;

import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.utils.ParticleUtil;
import net.year4000.hubitems.utils.Tracker;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
        Player player = event.getPlayer();
        if (!isItem(player) || isRightClick(event.getAction())) return;

        if (cost(player, info.mana())) {
            World world = player.getWorld();
            Egg egg = player.throwEgg();
            egg.setPassenger(world.spawnEntity(player.getEyeLocation().clone().add(0, 0.8, 0), EntityType.FIREBALL));
            new Tracker(world, egg.getEntityId(), ParticleUtil.Particles.FLAME);
        }
    }
}
