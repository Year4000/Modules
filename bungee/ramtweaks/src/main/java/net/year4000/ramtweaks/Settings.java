package net.year4000.ramtweaks;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.utilities.config.Comment;
import net.year4000.utilities.config.Config;
import net.year4000.utilities.config.InvalidConfigurationException;

import java.io.File;

@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends Config {
    private static Settings inst;

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
        if (inst == null) {
            inst = new Settings();
        }

        return inst;
    }

    @Comment("The URL to grab the locale from")
    private String url = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/ramtweaks/locales/";
}
