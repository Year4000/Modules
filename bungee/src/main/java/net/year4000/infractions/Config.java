package net.year4000.infractions;

import com.ewized.utilities.core.util.cache.QuickCache;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.io.File;

@Data
@EqualsAndHashCode(callSuper = false)
public class Config extends net.cubespace.Yamler.Config.Config {
    private static QuickCache<Config> inst = QuickCache.builder(Config.class).build();

    public Config() {
        try {
            CONFIG_HEADER = new String[] {"Infractions Configuration"};
            CONFIG_FILE = new File(Infractions.getInstance().getDataFolder(), "config.yml");
            init();
        } catch (InvalidConfigurationException e) {
            Infractions.log(e.getMessage());
        }
    }

    public static Config get() {
        return inst.get();
    }

    @Comment("Settings for default messages.")
    private String url = "https://git.year4000.net/year4000/locales/raw/master/net/year4000/accounts/locales/";
    private String link = "&6&lyear4000.net/player/%player%/infractions/";
}
