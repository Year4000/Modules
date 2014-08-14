package net.year4000.servermenu;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.servermenu.config.MenuBook;
import net.year4000.utilities.config.Comment;
import net.year4000.utilities.config.Config;
import net.year4000.utilities.config.InvalidConfigurationException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends Config {
    private static Settings inst;

    public Settings() {
        try {
            CONFIG_HEADER = new String[] {"Server Menu Settings"};
            CONFIG_FILE = new File(ServerMenu.getInst().getDataFolder(), "config.yml");
            init();
        } catch (InvalidConfigurationException e) {
            ServerMenu.log(e, true);
        }
    }

    public static Settings get() {
        if (inst == null) {
            inst = new Settings();
        }

        return inst;
    }

    @Comment("The url to pull the locales from")
    private String url = "https://git.year4000.net/year4000/locales/raw/master/net/year4000/servermenu/locales/";

    @Comment("The api server that we will grab the servers from")
    private String api = "https://api.year4000.net/servers/";

    @Comment("Should the menus have a return to hub button.")
    private boolean hub = false;

    @Comment("The group for the hubse")
    private String hubGroup = "us-hubs";

    @Comment("The menus")
    private List<MenuBook> menus = new ArrayList<>();
}
