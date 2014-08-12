package net.year4000.hubitems.items.staffs;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.year4000.hub.Hub;
import net.year4000.hubitems.items.Action;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.messages.Message;
import net.year4000.hubitems.utils.ParticleUtil;
import net.year4000.hubitems.utils.Tracker;
import net.year4000.worldback.WorldBackEvent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

@FunItemInfo(
    name = "corruptedstaff.name",
    icon = Material.IRON_HOE,
    description = "corruptedstaff.description",
    permission = {"sigma", "corruptedstaff.permission"},
    mana = 0.2F,
    action = Action.LEFT
)
public class CorruptedStaff extends FunItem {
    private Random rand = new Random();
    private Map<Integer, Player> skulls = new HashMap<>();
    private Set<Material> deadPatch = ImmutableSet.of(Material.NETHERRACK, Material.NETHER_BRICK, Material.SOUL_SAND);
    private Iterator<Material> deadPatches = Iterables.cycle(deadPatch).iterator();

    @EventHandler
    public void use(PlayerInteractEvent event) {
        if (!isItem(event.getAction(), event.getPlayer())) return;

        if (cost(event.getPlayer(), info.mana())) {
            WitherSkull entity = event.getPlayer().launchProjectile(WitherSkull.class);
            skulls.put(entity.getEntityId(), event.getPlayer());
            new Tracker(event.getPlayer().getWorld(), entity.getEntityId(), ParticleUtil.Particles.LARGE_SMOKE);
        }
    }

    /** When the skulls breaks send out an ice blast */
    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (skulls.containsKey(e.getEntity().getEntityId())) {
            Location loc = e.getEntity().getLocation();

            // set impact to snow/ice and add it to world back
            callSkullBlock(skulls.get(e.getEntity().getEntityId()), loc.getBlock().getRelative(BlockFace.DOWN));

            skulls.remove(e.getEntity().getEntityId());
        }
    }

    /** Send out an corrupted area */
    private void callSkullBlock(Player player, Block block) {
        if (Math.sqrt(block.getLocation().distanceSquared(block.getWorld().getSpawnLocation())) < Hub.SPAWN_PROTECTION) {
            player.sendMessage(" " + new Message(player).get("spawn.protect"));
            return;
        }

        for (BlockFace face : BlockFace.values()) {
            Block side = block.getRelative(face);

            if (!deadPatch.contains(side.getType()) && rand.nextBoolean() && !side.getType().equals(Material.AIR)) {
                Bukkit.getPluginManager().callEvent(new WorldBackEvent(side));

                if (side.getType().isSolid()) {
                    side.setType(deadPatches.next());

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.playEffect(side.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
                    }
                }
            }
        }
    }
}
