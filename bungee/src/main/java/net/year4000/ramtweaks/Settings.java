package net.year4000.ramtweaks;

import com.ewized.utilities.core.util.cache.QuickCache;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Config;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.io.File;

@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends Config {
    private static QuickCache<Settings> inst = QuickCache.builder(Settings.class).build();

    public Settings() {
        try {
            CONFIG_HEADER = new String[]{"RamTweaks Configuration"};
            CONFIG_FILE = new File(RamTweaks.getInst().getDataFolder(), "config.yml");
            init();
        } catch (InvalidConfigurationException e) {
            RamTweaks.debug(e, false);
        }
    }

    public static Settings get() {
        return inst.get();
    }

    @Comment("The URL to grab the locale from")
    private String url = "https://git.year4000.net/year4000/locales/raw/master/net/year4000/ramtweaks/locales/";
}
