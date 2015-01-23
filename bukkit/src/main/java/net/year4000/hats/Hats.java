package net.year4000.hats;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import org.bukkit.Skin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

@ModuleInfo(
    name = "Hats",
    version = "0.0.1",
    description = "Change your hat",
    authors = {"Year4000"}
)
@ModuleListeners({Hats.HatListener.class})
public class Hats extends BukkitModule {
    private static final String AVATAR_URL_BASE = "https://api.year4000.net/avatar/%s/128?json&compact";
    private static final Gson GSON = new Gson();
    private static final LoadingCache<String, String> avatars = CacheBuilder.newBuilder()
        .build(new CacheLoader<String, String>() {
            @Override
            public String load(String avatar) throws Exception {
                String url = new Date().getMonth() == 11 || new Date().getMonth() == 0 ? AVATAR_URL_BASE + "&santa" : AVATAR_URL_BASE;
                InputStream stream = new URL(String.format(url, avatar)).openStream();
                Avatar json = GSON.fromJson(new InputStreamReader(stream), Avatar.class);
                String base64 = json.avatar.substring(json.avatar.indexOf(",") + 1);

                return base64;
            }
        });

    public static class HatListener implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            event.getPlayer().setSkin(new Skin(avatars.getUnchecked(event.getPlayer().getName()), null));
        }
    }

    /** Represent the json object returned from api */
    public static class Avatar {
        private String avatar;
    }
}
