package net.year4000.ramtweaks;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.config.Comment;
import net.year4000.utilities.config.Config;
import net.year4000.utilities.config.InvalidConfigurationException;

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

    @Comment("Show ram stats to base on how much ram the server is using.")
    private boolean showRamStats = false;

}
