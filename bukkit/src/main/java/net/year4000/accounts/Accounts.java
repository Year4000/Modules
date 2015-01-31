package net.year4000.accounts;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.year4000.ducktape.bukkit.DuckTape;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.Callback;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ModuleInfo(
    name = "Accounts",
    version = "2.0",
    description = "Workaround for accounts until proper system is in place",
    authors = {"Year4000"}
)
@ModuleListeners({Accounts.LoginListener.class})
public class Accounts extends BukkitModule {
    private static final String MASTER_KEY = "97be6c1b-0d4a-4e01-9b13-8ff923a8a5cd";
    private static final String BASE_URL = "http://api.y4k.me/";
    private static final Gson gson = new Gson();
    private static final ConcurrentMap<String, Callback<Player>> accounts = new ConcurrentHashMap<>();

    public static class LoginListener implements Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onLogin(AsyncPlayerPreLoginEvent event) {
            String ip = BASE_URL + "accounts/" + event.getUniqueId() + "?key=" + MASTER_KEY;

            // Get player account
            try {
                URL url = new URL(ip);
                JsonObject object = gson.fromJson(new InputStreamReader(url.openStream()), JsonObject.class);
                accounts.put(event.getName(), (player, error) -> {
                    if (object.get("permissions") != null) {
                        JsonArray permissions = object.get("permissions").getAsJsonArray();
                        PermissionAttachment attachment = player.addAttachment(DuckTape.get());
                        permissions.forEach(element -> {
                            Permission permission = new Permission(element.getAsString());
                            attachment.setPermission(permission, true);
                        });
                        player.recalculatePermissions();
                    }
                });
            }
            // Account does not exist
            catch (IOException | NullPointerException ioe) {
                Accounts.debug(ioe, false);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onLogin(PlayerJoinEvent event) {
            Player player = event.getPlayer();

            Callback<Player> attach = accounts.remove(player.getName());

            if (attach != null) {
                attach.callback(player, null);
            }
        }
    }
}
