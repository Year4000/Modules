package net.year4000.viplogin;

import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

@ModuleInfo(
    name = "VIP Login",
    version = "1.0",
    description = "Only VIP can login to this server.",
    authors = {"Year4000"}
)
@ModuleListeners({VIPLogin.LoginListener.class})
public class VIPLogin extends BukkitModule {
    public static class LoginListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onLogin(PlayerLoginEvent event) {
            if (!event.getPlayer().hasPermission("theta")) {
                event.setKickMessage(MessageUtil.message(
                    "&eYou need VIP to login to &6%s \n %s",
                    Bukkit.getServerName(),
                    "&6&ohttps://www.year4000.net/page/shop/"
                ));
                event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            }
        }
    }
}
