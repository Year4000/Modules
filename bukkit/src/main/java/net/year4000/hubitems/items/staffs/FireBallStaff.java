package net.year4000.hubitems.items.staffs;

import net.year4000.hubitems.items.Action;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.utils.ParticleUtil;
import net.year4000.hubitems.utils.Tracker;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

@FunItemInfo(
    name = "fireballstaff.name",
    icon = Material.STONE_HOE,
    description = "fireballstaff.description",
    permission = {"mu", "pi", "sigma", "phi", "delta", "omega", "fireballstaff.permission"},
    mana = 0.1F,
    action = Action.LEFT
)
public class FireBallStaff extends FunItem {
    @EventHandler
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isItem(event.getAction(), player)) return;

        if (cost(player, info.mana())) {
            World world = player.getWorld();
            Fireball fireball = player.launchProjectile(Fireball.class);
            new Tracker(world, fireball.getEntityId(), ParticleUtil.Particles.FLAME);
        }
    }
}
