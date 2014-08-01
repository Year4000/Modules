package net.year4000.hubitems.items.staffs;

import com.google.common.collect.ImmutableSet;
import net.year4000.hub.Hub;
import net.year4000.hub.WorldBackEvent;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.messages.Message;
import net.year4000.hubitems.utils.ParticleUtil;
import net.year4000.hubitems.utils.Tracker;
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
    //permission = {"mu" , "icestaff.permission"},
    mana = 0.08F
)
public class IceStaff extends FunItem {
    Random rand = new Random();
    Map<Integer, Player> ice = new HashMap<>();
    Set<Material> icePatch = ImmutableSet.of(Material.ICE, Material.SNOW_BLOCK, Material.PACKED_ICE);

    @EventHandler
    public void use(PlayerInteractEvent event) {
        if (!isItem(event.getPlayer()) || isRightClick(event.getAction())) return;

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
            player.sendMessage(new Message(player).get("spawn.protect"));
            return;
        }

        Iterator<Material> patch = icePatch.iterator();

        for (BlockFace face : BlockFace.values()) {
            Block side = block.getRelative(face);

            if (!icePatch.contains(side.getType()) && rand.nextBoolean()) {
                Bukkit.getPluginManager().callEvent(new WorldBackEvent(side));

                if (patch.hasNext()) {
                    side.setType(patch.next());
                }
                else {
                    patch = icePatch.iterator();
                }

                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.playEffect(side.getLocation(), Effect.SNOWBALL_BREAK, 0);
                }
            }
        }
    }
}
