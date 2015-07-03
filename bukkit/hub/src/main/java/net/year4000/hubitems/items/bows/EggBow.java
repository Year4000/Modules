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
    permissionLocale = "eggbow.permission",
    permission = {"mu", "pi", "sigma", "phi", "delta", "omega"},
    mana = 0.05F,
    action = Action.RIGHT
)
public class EggBow extends FunItem {
    @EventHandler
    public void use(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!isItem(org.bukkit.event.block.Action.RIGHT_CLICK_AIR, player)) return;

        if (cost(player, info.mana())) {
            World world = player.getWorld();
            Vector vector = new Vector().copy(event.getProjectile().getVelocity());
            Location loc = event.getEntity().getLocation().clone().add(0, event.getEntity().getEyeHeight(), 0).add(0, 0.7, 0);
            Entity entity = world.spawnEntity(loc, EntityType.EGG);

            entity.setVelocity(vector);
            new Tracker(world, entity.getEntityId(), Effect.HEART);
        }

        event.setCancelled(true); // don't use the arrow in the inventory
        SchedulerUtil.runSync(() -> player.updateInventory());
    }
}
