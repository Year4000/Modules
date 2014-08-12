package net.year4000.hubitems.items.bows;

import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.hub.Hub;
import net.year4000.hubitems.items.Action;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.messages.Message;
import net.year4000.hubitems.utils.ParticleUtil;
import net.year4000.hubitems.utils.Tracker;
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

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

@FunItemInfo(
    name = "tntbow.name",
    icon = Material.BOW,
    description = "tntbow.description",
    permission = {"pi" , "tntbow.permission"},
    mana = 0.25F,
    action = Action.RIGHT
)
public class TNTBow extends FunItem {
    private Random rand = new Random();
    private Map<Integer, Player> tnts = new WeakHashMap<>();

    @EventHandler
    public void use(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!isItem(org.bukkit.event.block.Action.RIGHT_CLICK_AIR, player)) return;

        if (cost(player, info.mana())) {
            World world = player.getWorld();
            Vector vector = new Vector().copy(event.getProjectile().getVelocity());
            Location loc = event.getEntity().getLocation().clone().add(0, event.getEntity().getEyeHeight(), 0).add(0, 0.7, 0);
            Entity entity = world.spawnEntity(loc, EntityType.PRIMED_TNT);
            tnts.put(entity.getEntityId(), player);

            entity.setVelocity(vector);
            new Tracker(world, entity.getEntityId(), ParticleUtil.Particles.CLOUD);
        }

        event.setCancelled(true); // don't use the arrow in the inventory
        SchedulerUtil.runSync(() -> player.updateInventory());
    }

    @EventHandler
    public void happy(EntityExplodeEvent event) {
        if (tnts.containsKey(event.getEntity().getEntityId()) && event.getEntity().getLocation().distance(event.getEntity().getWorld().getSpawnLocation()) < Hub.SPAWN_PROTECTION) {
            Player player = tnts.get(event.getEntity().getEntityId());
            player.sendMessage(" " + new Message(player).get("spawn.protect"));
            tnts.remove(event.getEntity().getEntityId());
        }

        event.getEntity().getWorld().getEntities().stream()
            .filter(entity -> entity.getLocation().distance(event.getLocation()) < 6)
            .forEach(entity -> entity.setVelocity(new Vector(rand.nextDouble(), 1.3 + rand.nextDouble(), rand.nextDouble())));
    }
}
