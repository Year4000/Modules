package net.year4000.hubitems.items.passive;

import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;
import net.year4000.bages.BadgeManager;
import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.items.PassiveState;
import net.year4000.utilities.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

@FunItemInfo(
    name = "auras.name",
    icon = Material.BLAZE_ROD,
    description = "auras.description",
    permission = {"theta", "auras.permission"},
    passive = PassiveState.ALLWAYS_ON
)
public class Auras extends FunItem {
    private static BadgeManager manager = new BadgeManager();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission(info.permission()[0])) return;

        BadgeManager.Badges badges = manager.findBadge(player);

        sendPacket(player, badges);
    }

    /** Send out the packet to allow for the potion effect */
    private void sendPacket(Player player, BadgeManager.Badges badge) {
        Vector loc = player.getLocation().toVector();
        String type = player.getLocation().getBlock().isLiquid() ? "bubble" : "mobSpell";
        PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(type, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0.3F, 0.3F, 0.3F, badgeFromHex(badge), 3);
        // BELLOW CODE IS TEST CODE FOR HACKING THE modSpell COLOR
        //PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles("instantSpell", (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0F, 0F, 0F, (float) badgeFromHex(badge), 3);
        //Effect e = Effect.POTION_BREAK;
        //int data = CraftEffect.getDataValue(e, new Potion(PotionType.FIRE_RESISTANCE));
        //PacketPlayOutWorldEvent particles = new PacketPlayOutWorldEvent(e.getId(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), data, false);
        //PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(e.getName(), (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0F, 0F, 0F, data, 3);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getLocation().distance(player.getLocation()) > 50) continue;

            ((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(particles);
        }
    }

    /** Convert the ChatColor hex number to an int */
    private int badgeFromHex(BadgeManager.Badges badges) {
        return Integer.valueOf(badges.getColor().toString().replace(ChatColor.COLOR_CHAR + "", ""), 16);
    }
}
