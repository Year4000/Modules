package net.year4000.hubitems.utils;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Tracker {
    private static List<Reference<Tracker>> trackers = new CopyOnWriteArrayList<>();
    private World world;
    private int id;
    private ParticleUtil.Particles particle;

    public Tracker(World world, int id, ParticleUtil.Particles particle) {
        this.world = world;
        this.id = id;
        this.particle = particle;
        trackers.add(new SoftReference<>(this));
    }

    public static class TrackerRunner implements Runnable {
        @Override
        public void run() {
            for (Tracker track : trackers.stream().map(Reference::get).collect(Collectors.toList())) {
                if (track == null) continue;

                List<Entity> shoot = track.world.getEntities().stream()
                    .filter(id -> id.getEntityId() == track.id)
                    .collect(Collectors.toList());

                if (shoot.size() == 0) {
                    continue;
                }

                shoot.forEach(e -> ParticleUtil.sendPackets(track.particle, e.getLocation()));
            }
        }
    }
}