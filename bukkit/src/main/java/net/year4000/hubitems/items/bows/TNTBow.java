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
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

@FunItemInfo(
    name = "tntbow.name",
    icon = Material.BOW,
    description = "tntbow.description",
    permission = {"pi" , "tntbow.permission"},
    mana = 0.25F
)
public class TNTBow extends FunItem {
    @EventHandler
    public void use(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!isItem(player)) return;

        if (cost(player, info.mana())) {
            World world = player.getWorld();
            Vector vector = new Vector().copy(event.getProjectile().getVelocity());
            Location loc = event.getEntity().getLocation().clone().add(0, event.getEntity().getEyeHeight(), 0).add(0, 0.7, 0);
            Entity entity = world.spawnEntity(loc, EntityType.PRIMED_TNT);

            entity.setVelocity(vector);
            new Tracker(world, entity.getEntityId(), ParticleUtil.Particles.CLOUD);
        }

        event.setCancelled(true); // don't use the arrow in the inventory
        Bukkit.getScheduler().runTask(DuckTape.get(), () -> player.updateInventory());
    }

    @EventHandler
    public void happy(EntityExplodeEvent event) {
        event.getEntity().getWorld().getEntities().stream().filter(entity -> entity.getLocation().distance(event.getLocation()) < 6).forEach(entity -> entity.setVelocity(entity.getVelocity().multiply(3)));
    }
}
