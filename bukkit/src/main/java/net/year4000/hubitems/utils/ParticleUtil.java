package net.year4000.hubitems.utils;

import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticleUtil {
    public enum Particles {
        BUBBLE("bubble"),
        CLOUD("cloud"),
        FIREWORKS("fireworksSpark"),
        FLAME("flame"),
        HEART("heart"),
        HUGE_EXPLOSION("hugeexplosion"),
        LARGE_EXPLOSION("largeexplode"),
        LARGE_SMOKE("largesmoke"),
        LAVA("lava"),
        LAVA_DRIP("dripLava"),
        PORTAL("portal"),
        RED_DUST("reddust"),
        WATER_DRIP("dripWater");

        private String name;

        private Particles(String name) {
            this.name = name;
        }
    }

    public static void sendPacket(Player player, Particles particle, Location location) {
        sendPacket(player, particle, location.getX(), location.getY(), location.getZ());
    }

    public static void sendPackets(Particles particle, Location location) {
        sendPackets(particle, location.getX(), location.getY(), location.getZ());
    }

    public static void sendPacket(Player player, Particles particle, double x, double y, double z) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle.name, (float) x, (float) y, (float) z, 0, 0, 0, 0, 1);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendPackets(Particles particle, double x, double y, double z) {
        sendPackets(particle, x, y, z, 0, 0, 0, 1);
    }

    public static void sendPackets(Particles particle, double x, double y, double z, double xOffset, double zOffset, double yOffset, int particleNumber) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle.name, (float) x, (float) y, (float) z, (float) xOffset, (float) yOffset, (float) zOffset, 0F, particleNumber);
        Location loc = new Location(null, x, y, z);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (loc.getWorld() == null) {
                loc.setWorld(player.getWorld());
            }
            if (player.getLocation().distance(loc) < 50) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

}
