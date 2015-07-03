/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.hubitems.items.bows;

import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import net.year4000.hubitems.items.Action;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.utils.Tracker;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.WeakHashMap;

@FunItemInfo(
    name = "enderbow.name",
    icon = Material.BOW,
    description = "enderbow.description",
    permissionLocale = "enderbow.permission",
    permission = {"theta", "mu", "pi", "sigma", "phi", "delta", "omega"},
    mana = 0.15F,
    action = Action.RIGHT
)
public class EnderBow extends FunItem {
    private Map<Integer, Player> enderPearls = new WeakHashMap<>();

    /** Change the way bows act */
    @EventHandler(ignoreCancelled = true)
    public void onBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!isItem(org.bukkit.event.block.Action.RIGHT_CLICK_AIR, player)) return;

        if (cost(player, info.mana())) {
            World world = event.getEntity().getWorld();
            Vector vector = new Vector().copy(event.getProjectile().getVelocity());
            Location loc = event.getEntity().getLocation().clone().add(0, event.getEntity().getEyeHeight(), 0).add(0, 0.7, 0);
            Entity entity = world.spawn(loc, EnderPearl.class);

            entity.setVelocity(vector);
            new Tracker(world, entity.getEntityId(), Effect.PORTAL);
            enderPearls.put( entity.getEntityId(), player);
        }

        event.setCancelled(true); // don't use the arrow in the inventory
        SchedulerUtil.runSync(() -> player.updateInventory());
    }

    /** Teleport the player if the player shot an ender pearl */
    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (enderPearls.containsKey(e.getEntity().getEntityId())) {
            enderPearls.get(e.getEntity().getEntityId()).teleport(e.getEntity().getLocation());
            enderPearls.remove(e.getEntity().getEntityId());
        }
    }
}
