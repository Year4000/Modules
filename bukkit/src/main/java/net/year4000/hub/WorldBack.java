package net.year4000.hub;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.year4000.ducktape.bukkit.DuckTape;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFallEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Deque;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

public class WorldBack implements Listener, Runnable {
    private static final Random rand = new Random();
    private BukkitTask task;
    private Deque<BlockBack> blocks = new ConcurrentLinkedDeque<>();
    private Set<Material> blackList = ImmutableSet.of(Material.TNT, Material.FIRE);

    public WorldBack() {
        task = Bukkit.getScheduler().runTaskTimer(DuckTape.get(), this, 20, 20);
    }

    // Block Listeners //

    @EventHandler(ignoreCancelled = true)
    public void onStateChange(BlockBreakEvent event) {
        if (event.getPlayer().isSneaking()) return;
        filter(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onStateChange(EntityExplodeEvent event) {
        event.blockList().forEach(this::filter);
        event.setYield(0);
    }

    @EventHandler(ignoreCancelled = true)
    public void onStateChange(BlockFallEvent event) {
        filter(event.getBlock());
    }

    @EventHandler
    public void onStateChange(WorldBackEvent event) {
        filter(event.getBlock());
    }

    /** Filter the blocks that should regen */
    public void filter(Block block) {
        if (!blackList.contains(block.getType())) {
            blocks.add(new BlockBack(block));
        }
    }

    // Revert The Block Back //

    @Override
    public void run() {
        if (blocks.size() > 0) {
            for (int i = 0; i < Math.sqrt(blocks.size()) / 2; i++) {
                if (blocks.size() > 0) {
                    blocks.pop().revert();
                }
            }
        }
    }

    /** Stop this task and revert all blocks */
    public void cancel() {
        task.cancel();
        blocks.forEach(BlockBack::revert);
    }

    /**
     * The class that stores the data needed to regen the
     * block back to its old state.
     */
    @Data
    @AllArgsConstructor
    class BlockBack {
        private World world;
        private Vector location;
        private Material block;
        private byte data;

        public BlockBack(Block block) {
            this(block.getWorld(), block.getLocation().toVector(), block.getType(), block.getData());
        }

        private Location splater(Location loc) {
            return loc.clone().add(new Vector(rand.nextDouble(), rand.nextDouble() + 0.5, rand.nextDouble()));
        }

        /** Revert the block back to its old state */
        public void revert() {
            Location loc = new Location(world, location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Block oldBlock = world.getBlockAt(loc);
            //oldBlock.setType(block);
            oldBlock.setTypeIdAndData(block.getId(), data, true);

            // Cool Effect
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Math.sqrt(player.getLocation().distanceSquared(loc)) < 50) {
                    world.playSound(loc, Sound.ITEM_PICKUP, (float) 0.2, (float) 0.1);

                    for (int i = 0; i < 10; i++) {
                        player.playEffect(splater(loc), Effect.VOID_FOG, 1);
                    }
                }
            }
        }
    }
}
