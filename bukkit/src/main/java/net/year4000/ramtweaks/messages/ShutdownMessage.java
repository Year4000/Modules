package net.year4000.ramtweaks.messages;

import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.ducktape.bukkit.DuckTape;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ShutdownMessage implements Runnable {
    private BukkitTask task;
    private CommandSender console = Bukkit.getConsoleSender();
    private int countdown;

    public ShutdownMessage(int time) {
        countdown = time;
        task = Bukkit.getScheduler().runTaskTimer(DuckTape.get(), this, 20, 20);
    }

    @Override
    public void run() {
        String type = countdown > 1 ? "restart.countdown.plural" : "restart.countdown.single";

        if (countdown == 0) {

            console.sendMessage(MessageUtil.message(new Message(console).get("restart.message")));
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(MessageUtil.message(new Message(player).get("restart.message")));
            }
            task.cancel();
            Bukkit.shutdown();
        }
        else if (countdown > 0) {
            console.sendMessage(MessageUtil.message(new Message(console).get(type, countdown)));
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(MessageUtil.message(MessageUtil.message(new Message(player).get(type, countdown))));
            }
        }
        countdown--;
    }
}
