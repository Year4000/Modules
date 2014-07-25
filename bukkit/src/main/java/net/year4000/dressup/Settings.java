package net.year4000.dressup;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Config;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends Config {
    public static final String HAT = "hat", CHEST = "shirt", PANTS = "pants", BOOTS = "boots";
    private static Settings inst;

    public Settings() {
        try {
            CONFIG_HEADER = new String[] {"Dress Up Config"};
            CONFIG_FILE = new File(DressUp.getInst().getDataFolder(), "config.yml");
            init();
        } catch (InvalidConfigurationException e) {
            DressUp.log(e, false);
        }
    }

    public static Settings get() {
        if (inst == null) {
            inst = new Settings();
        }

        return inst;
    }

    @Comment("The url to get the locales from.")
    private String url = "https://git.year4000.net/year4000/locales/raw/master/net/year4000/dressup/locales/";

    @Comment("The location to store the armor db.")
    private String storage = ".";

    @Comment("The storage for the items")
    private Map<String, List<ArmorItem>> items = new HashMap<String, List<ArmorItem>>() {{
        put(HAT, Arrays.asList(
            new ArmorItem("leather_helmet", 0, ""),
            new ArmorItem("chainmail_helmet", 0, ""),
            new ArmorItem("iron_helmet", 0, ""),
            new ArmorItem("gold_helmet", 0, ""),
            new ArmorItem("diamond_helmet", 0, ""),
            new ArmorItem("glass", 0, ""),
            new ArmorItem("pumpkin", 0, ""),
            new ArmorItem("jack_o_lantern", 0, ""),
            new ArmorItem("glowstone", 0, "")
        ));

        put(CHEST, Arrays.asList(
            new ArmorItem("leather_checkplate", 0, ""),
            new ArmorItem("chainmail_checkplate", 0, ""),
            new ArmorItem("iron_checkplate", 0, ""),
            new ArmorItem("gold_checkplate", 0, ""),
            new ArmorItem("diamond_checkplate", 0, "")
        ));

        put(PANTS, Arrays.asList(
            new ArmorItem("leather_leggings", 0, ""),
            new ArmorItem("chainmail_leggings", 0, ""),
            new ArmorItem("iron_leggings", 0, ""),
            new ArmorItem("gold_leggings", 0, ""),
            new ArmorItem("diamond_leggings", 0, "")
        ));

        put(BOOTS, Arrays.asList(
            new ArmorItem("leather_boots", 0, ""),
            new ArmorItem("chainmail_boots", 0, ""),
            new ArmorItem("iron_boots", 0, ""),
            new ArmorItem("gold_boots", 0, ""),
            new ArmorItem("diamond_boots", 0, "")
        ));
    }};
}
