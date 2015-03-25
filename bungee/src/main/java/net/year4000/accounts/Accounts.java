package net.year4000.accounts;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.bungee.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.scheduler.SchedulerManager;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "Accounts",
    version = "2.0",
    description = "Workaround for accounts until proper system is in place",
    authors = {"Year4000"}
)
@ModuleListeners({Accounts.LoginListener.class})
public class Accounts extends BungeeModule {
    private static final String MASTER_KEY = "97be6c1b-0d4a-4e01-9b13-8ff923a8a5cd";
    private static final String BASE_URL = "https://api.year4000.net/";
    private static final Gson gson = new Gson();
    // Give players ex per hour of play time
    static {
        ProxyServer server = ProxyServer.getInstance();
        server.getScheduler().schedule(DuckTape.get(), () -> {
            server.getPlayers().forEach(player -> {

            });
        },0, 30, TimeUnit.SECONDS);
    }

    public static class LoginListener implements Listener {
        @EventHandler
        public void onLogin(PreLoginEvent event) {
            String ip = BASE_URL + "accounts/" + event.getConnection().getName() + "?key=" + MASTER_KEY;
            String connectingIp = event.getConnection().getAddress().getAddress().getHostAddress();
            String uuid = null;

            // Get player account
            try {
                URL url = new URL(ip);
                JsonObject object = gson.fromJson(new InputStreamReader(url.openStream()), JsonObject.class);

                if (object.get("last_ip") != null) {
                    ip = object.get("last_ip").getAsString();
                }

                if (object.get("minecraft") != null && object.get("minecraft").getAsJsonObject().get("uuid") != null) {
                    uuid = object.get("minecraft").getAsJsonObject().get("uuid").getAsString();
                }

                try (Socket socket = new Socket("sessionserver.mojang.com", 443)) {
                    if (socket.isConnected()) {
                        Accounts.debug("Session servers online!");
                    }
                }
            }
            // Session servers offline
            catch (SocketException se) {
                if (connectingIp.equals(ip) && uuid != null) {
                    event.getConnection().setOnlineMode(false);
                    event.getConnection().setUniqueId(UUID.fromString(uuid));
                }
                else {
                    event.setCancelled(true);
                    event.setCancelReason(se.getMessage());
                }
            }
            // Account does not exist
            catch (IOException ioe) {
                Accounts.debug(ioe, false);
            }
        }

        @EventHandler
        public void onLogin(LoginEvent event) {
            // Send login to the API server
            try {
                // Will throw if account does not exist
                new URL(BASE_URL + "accounts/" + event.getConnection().getUniqueId()).openStream();

                // Account exists lets login
                URL url = new URL(BASE_URL + "accounts/" + event.getConnection().getUniqueId() + "/login?key=" + MASTER_KEY);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf8");
                connection.setRequestProperty("User-Agent", "Year4000 Account Login - BungeeCord");
                connection.setDoOutput(true);

                try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                    // Create JSON POST
                    JsonObject login = new JsonObject();
                    login.addProperty("type", "minecraft");
                    login.addProperty("ip", event.getConnection().getAddress().getAddress().getHostAddress());
                    JsonObject meta = new JsonObject();
                    meta.addProperty("username", event.getConnection().getName());
                    meta.addProperty("version", event.getConnection().getVersion());
                    login.add("meta", meta);

                    gson.toJson(login, writer);
                }

                if (connection.getResponseCode() != 200) {
                    throw new IOException(connection.getResponseMessage());
                }
            }
            catch (IOException ioe) {
                Accounts.log(ioe, true);

                // Send new account to API
                try {
                    // Create accounts
                    URL url = new URL(BASE_URL + "accounts?key=" + MASTER_KEY);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf8");
                    connection.setRequestProperty("User-Agent", "Year4000 Account Creation - BungeeCord");
                    connection.setDoOutput(true);

                    try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                        // Create JSON POST
                        JsonObject create = new JsonObject();
                        create.addProperty("last_ip", event.getConnection().getAddress().getAddress().getHostAddress());
                        JsonObject minecraft = new JsonObject();
                        minecraft.addProperty("username", event.getConnection().getName());
                        minecraft.addProperty("uuid", event.getConnection().getUniqueId().toString());
                        minecraft.addProperty("version", event.getConnection().getVersion());
                        create.add("minecraft", minecraft);

                        gson.toJson(create, writer);
                    }

                    if (connection.getResponseCode() != 200) {
                        throw new Exception(connection.getResponseMessage());
                    }
                }
                // When all fails deny login and show exception error
                catch (Exception e) {
                    event.setCancelReason(e.getMessage());
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onLogin(PostLoginEvent event) {
            String ip = BASE_URL + "accounts/" + event.getPlayer().getUniqueId() + "?key=" + MASTER_KEY;

            // Get player account
            try {
                URL url = new URL(ip);
                JsonObject object = gson.fromJson(new InputStreamReader(url.openStream()), JsonObject.class);
                if (object.get("permissions") != null) {
                    JsonArray permissions = object.get("permissions").getAsJsonArray();
                    permissions.forEach(element -> {
                        event.getPlayer().addGroups(element.getAsString());
                        event.getPlayer().setPermission(element.getAsString(), true);
                    });
                }
            }
            // Account does not exist
            catch (IOException ioe) {
                Accounts.debug(ioe, false);
            }
        }
    }
}
