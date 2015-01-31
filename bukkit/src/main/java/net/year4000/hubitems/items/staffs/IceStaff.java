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
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

@FunItemInfo(
    name = "icestaff.name",
    icon = Material.DIAMOND_HOE,
    description = "icestaff.description",
    permission = {"mu", "pi", "sigma", "phi", "delta", "omega", "icestaff.permission"},
    mana = 0.08F,
    action = Action.LEFT
)
public class IceStaff extends FunItem {
    private Random rand = new Random();
    private Map<Integer, Player> ice = new HashMap<>();
    private Set<Material> icePatch = ImmutableSet.of(Material.ICE, Material.SNOW_BLOCK, Material.PACKED_ICE);
    private Iterator<Material> icePatches = Iterables.cycle(icePatch).iterator();

    @EventHandler
    public void use(PlayerInteractEvent event) {
        if (!isItem(event.getAction(), event.getPlayer())) return;

        if (cost(event.getPlayer(), info.mana())) {
            Snowball entity = event.getPlayer().launchProjectile(Snowball.class);
            ice.put(entity.getEntityId(), event.getPlayer());
            new Tracker(event.getPlayer().getWorld(), entity.getEntityId(), ParticleUtil.Particles.WATER_DRIP);
        }
    }

    /** When the snow ball breaks send out an ice blast */
    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (ice.containsKey(e.getEntity().getEntityId())) {
            Location loc = e.getEntity().getLocation();

            // set impact to snow/ice and add it to world back
            callIceBlock(ice.get(e.getEntity().getEntityId()), loc.getBlock().getRelative(BlockFace.DOWN));

            ice.remove(e.getEntity().getEntityId());
        }
    }

    /** Send out an ice patch */
    private void callIceBlock(Player player, Block block) {
        if (block.getLocation().distance(block.getWorld().getSpawnLocation()) < Hub.SPAWN_PROTECTION) {
            player.sendMessage(" " + new Message(player).get("spawn.protect"));
            return;
        }

        for (BlockFace face : BlockFace.values()) {
            Block side = block.getRelative(face);

            if (!icePatch.contains(side.getType()) && rand.nextBoolean()) {
                Bukkit.getPluginManager().callEvent(new WorldBackEvent(side));

                if (side.getType().isSolid() || side.getType() == Material.AIR) {
                    side.setType(icePatches.next());


                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.playEffect(side.getLocation(), Effect.SNOWBALL_BREAK, 0);
                    }
                }
            }
        }
    }
}
