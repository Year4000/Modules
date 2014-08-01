package net.year4000.hubitems.items.bows;

import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.utils.ParticleUtil;
import net.year4000.hubitems.utils.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

@FunItemInfo(
    name = "eggbow.name",
    icon = Material.BOW,
    description = "eggbow.description",
    permission = {"mu" , "eggbow.permission"},
    mana = 0.05F
)
public class EggBow extends FunItem {
    @EventHandler
    public void use(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!isItem(player)) return;

        if (cost(player, info.mana())) {
            World world = player.getWorld();
            Vector vector = new Vector().copy(event.getProjectile().getVelocity());
            Location loc = event.getEntity().getLocation().clone().add(0, event.getEntity().getEyeHeight(), 0).add(0, 0.7, 0);
            Entity entity = world.spawnEntity(loc, EntityType.EGG);

            entity.setVelocity(vector);
            new Tracker(world, entity.getEntityId(), ParticleUtil.Particles.HEART);
        }

        event.setCancelled(true); // don't use the arrow in the inventory
        Bukkit.getScheduler().runTask(DuckTape.get(), () -> player.updateInventory());
    }
}