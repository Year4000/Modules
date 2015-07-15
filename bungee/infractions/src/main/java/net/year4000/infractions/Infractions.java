/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.infractions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.bungee.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.bungee.MessageUtil;
import net.year4000.utilities.redis.RedisMessaging;
import redis.clients.jedis.JedisPool;

import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Preconditions.checkNotNull;

@ModuleInfo(
    name = "Infractions",
    version = "1.3",
    description = "Temp infractions until Account plugin is made.",
    authors = {"Year4000"}
)
@ModuleListeners({JoinListener.class})
public class Infractions extends BungeeModule {
    @Getter private static Infractions instance;
    @Getter private static APIStorage storage;
    private JedisPool redis = new JedisPool("internal.api.year4000.net");
    private RedisMessaging messaging = new RedisMessaging(redis);
    private final String CHANNEL = "year4000.infractions.channel";

    public void load() {
        instance = this;
    }

    public void enable() {
        storage = new APIStorage();

        // Register Listeners
        new JoinListener();
        // Enable Commands
        registerCommand(Commands.class);

        // Subscribe to channel
        messaging.subscribe(CHANNEL, data -> {
            try {
                JsonObject object = new Gson().fromJson(data, JsonObject.class);
                checkState(object.has("uuid"));
                checkState(object.has("message"));
                UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                Optional<ProxiedPlayer> player = Optional.ofNullable(ProxyServer.getInstance().getPlayer(uuid));

                player.ifPresent(proxied -> proxied.disconnect(createMessage(proxied, object.get("message").getAsString())));
            }
            catch (JsonParseException e) {
                log("Incoming data invalid: " + e.getMessage());
            }
        });
    }

    /**
     * Create a disconnect message to tell the user their account can't login.
     * @param player The player's name.
     * @param message The message to show.
     * @return Disconnect message.
     */
    public static BaseComponent[] createMessage(ProxiedPlayer player, String message) {
        String link = Settings.get().getLink().replaceAll("%player%", player.getName());
        return MessageUtil.message(message + "\n\n" + new Message(player).get("default.notice") + "\n" + link);
    }

    /** Send the message in the channel to process it */
    public void sendMessage(ProxiedPlayer player, String message) {
        checkNotNull(player);
        JsonObject object = new JsonObject();
        object.addProperty("uuid", player.getUniqueId().toString());
        object.addProperty("message", checkNotNull(message));
        messaging.publish(CHANNEL, object.toString());
    }
}
