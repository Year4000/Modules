package net.year4000.backstrap;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import org.bukkit.Skin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Base64;
import java.util.Map;

@ModuleInfo(
    name = "BackStrap",
    version = "0.0.1",
    description = "Shh...",
    authors = {"Year4000"}
)
@ModuleListeners({BackStrap.BackListener.class})
public class BackStrap extends BukkitModule {
    private static final Gson gson = new Gson();

    public static class BackListener implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            Skin old = event.getPlayer().getSkin();
            EncodedSkin skin = gson.fromJson(new String(Base64.getMimeDecoder().decode(old.getData())), EncodedSkin.class);
            skin.textures.put("CAPE", new EncodedSkinURL("http://textures.minecraft.net/texture/3f688e0e699b3d9fe448b5bb50a3a288f9c589762b3dae8308842122dcb81"));
            //Skin cape = new Skin(new String(Base64.getMimeEncoder().encode(gson.toJson(skin).getBytes())), "");
            Skin cape = new Skin("eyJ0aW1lc3RhbXAiOjE0MjMyMDMzNDk2MTUsInByb2ZpbGVJZCI6IjYxNjk5YjJlZDMyNzRhMDE5ZjFlMGVhOGMzZjA2YmM2IiwicHJvZmlsZU5hbWUiOiJEaW5uZXJib25lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2NkNmJlOTE1YjI2MTY0M2ZkMTM2MjFlZTRlOTljOWU1NDFhNTUxZDgwMjcyNjg3YTNiNTYxODNiOTgxZmI5YSJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2Y2ODhlMGU2OTliM2Q5ZmU0NDhiNWJiNTBhM2EyODhmOWM1ODk3NjJiM2RhZTgzMDg4NDIxMjJkY2I4MSJ9fX0=", "");
            event.getPlayer().setSkin(cape);
        }
    }

    public static class EncodedSkin {
        private long timestamp;
        private String profileId;
        private String profileName;
        private Map<String, EncodedSkinURL> textures;
    }

    @AllArgsConstructor
    public static class EncodedSkinURL {
        private String url;
    }
}
