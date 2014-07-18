package net.year4000.serverlist;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Config;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends Config {
    public Settings() {
        try {
            CONFIG_HEADER = new String[]{"ServerList Configuration"};
            CONFIG_FILE = new File(ServerList.getInst().getDataFolder(), "config.yml");
            init();
        } catch (InvalidConfigurationException e) {
            ServerList.log("The config has an error in it could not init it.");
            ServerList.debug(e, false);
        }
    }

    public Settings(boolean unitTest) {}

    @Comment("The cache time in sec")
    private int cache = 5;

    @Comment("The locales that have been added")
    private List<String> locales = new ArrayList<String>() {{
        add("en_US");
    }};

    @Comment("The prefix for the to line.")
    private String prefix = "&3[&bY4K&3]&r ";

    @Comment("What to show on the bottom line if a player is not known.")
    private String noPlayer = "&7Join us today!";

    @Comment("What to show on the bottom line when a player is known.")
    private String player = "&7Welcome back {player}!";

    @Comment("The list to show when a known player hovers over the sample.")
    private List<String> players = new ArrayList<String>() {{
        add("&6====================");
        add("&7Welcome!");
        add("&6====================");
    }};

    @Comment("The list to pick messages from according to a specific date.")
    private HashMap<String, List<String>> messages = new HashMap<String, List<String>>() {{
        // Messages to be displayed for all dates
        put("*/*/*", new ArrayList<String>() {{
            add("&6Join our Survival Server!");
            add("&6Join our Creative Server!");
            add("&6Join our Games Server!");
        }});

        // Messages to display on christmas
        put("12/25/*", new ArrayList<String>() {{
            add("&6Merry Christmas!");
            add("&6Time to open presents!");
        }});

        // Messages to display on new years
        put("1/1/*", new ArrayList<String>() {{
            add("&6Happy New Year!!");
        }});
    }};
}
