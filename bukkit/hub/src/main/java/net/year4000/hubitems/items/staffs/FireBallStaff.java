/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.hubitems.items.staffs;

import net.year4000.hubitems.items.Action;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.utils.Tracker;
import org.bukkit.Effect;
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
    permissionLocale = "fireballstaff.permission",
    permission = {"mu", "pi", "sigma", "phi", "delta", "omega"},
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
            new Tracker(world, fireball.getEntityId(), Effect.FLAME);
        }
    }
}
