package net.year4000.infractions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.utilities.config.Comment;
import net.year4000.utilities.config.InvalidConfigurationException;

import java.io.File;

@Data
@EqualsAndHashCode(callSuper = false)
public class Config extends net.year4000.utilities.config.Config {
    private static Config inst;

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
        if (inst == null) {
            inst = new Config();
        }

        return inst;
    }

    @Comment("Settings for default messages.")
    private String url = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/accounts/locales/";
    private String link = "&6&lyear4000.net/player/%player%/infractions/";
}
