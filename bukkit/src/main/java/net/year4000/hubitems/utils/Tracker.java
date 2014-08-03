package net.year4000.hubitems.utils;

import net.year4000.ducktape.bukkit.utils.SchedulerUtil;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Tracker implements Runnable {
    private BukkitTask task;
    private World world;
    private int id;
    private ParticleUtil.Particles particle;

    public Tracker(World world, int id, ParticleUtil.Particles particle) {
        this.world = world;
        this.id = id;
        this.particle = particle;
        task = SchedulerUtil.repeatSync(this, (long) 0.1, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        List<Entity> shoot = world.getEntities().stream()
            .filter(id -> id.getEntityId() == this.id)
            .collect(Collectors.toList());

        if (shoot.size() == 0) {
            task.cancel();
            return;
        }

        shoot.forEach(e -> ParticleUtil.sendPackets(particle, e.getLocation()));
    }
}