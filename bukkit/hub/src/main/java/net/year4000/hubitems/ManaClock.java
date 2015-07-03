package net.year4000.hubitems;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.WeakHashMap;

public class ManaClock implements Runnable {
    @Getter
    private static Map<Player, Boolean> isReady = new WeakHashMap<>();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getExp() < 1F && isReady.get(player) != null) {
                player.setExp(player.getExp() + 0.001F);
            }
        }
    }
}
