package net.year4000.servermenu;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.year4000.utilities.configs.Config;
import net.year4000.utilities.configs.ConfigURL;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigURL(value = "https://api.year4000.net/configs/menus", config = Settings.class)
public class Settings extends Config<Settings> {
    private static Settings inst;

    /** Get the instance of this object */
    public static Settings get() {
        if (inst == null) {
            Settings settings = new Settings();
            inst = settings.getInstance(settings);
        }

        return inst;
    }

    // Config Options

    /** The locale url */
    private String locales;
    /** The regions that are supported */
    private String[] regions;
    /** The menus that are views */
    private Menu[] menus;

    @Getter
    @ToString
    private static class Menu {
        /** The name of this menu */
        private String name;
        /** The group suffix in combination with region */
        @SerializedName("group_suffix")
        private String groupSuffix;
        /** The Minecraft material to use as an icon */
        private String icon;
        /** Is this view a MapNodes server group */
        @SerializedName("map_nodes")
        private boolean mapNodes;
    }
}
