package net.year4000.dressup;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.utilities.bukkit.BukkitUtil;
import net.year4000.utilities.config.Comment;
import net.year4000.utilities.config.Config;

import java.util.*;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends Config {
    public static final String HAT = "hat", CHEST = "shirt", PANTS = "pants", BOOTS = "boots";
    private static Settings inst;

    public Settings() {
        /*try {
            CONFIG_HEADER = new String[] {"Dress Up Config"};
            CONFIG_FILE = new File(DressUp.getInst().getDataFolder(), "config.yml");
            init();
        } catch (InvalidConfigurationException e) {
            DressUp.log(e, false);
        }*/
    }

    public static Settings get() {
        if (inst == null) {
            inst = new Settings();
        }

        return inst;
    }

    @Comment("The url to get the locales from.")
    private String url = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/dressup/locales/";

    @Comment("The location to store the armor db.")
    private String storage = ".";

    @Comment("The storage for the items")
    private Map<String, List<ArmorItem>> items = new HashMap<String, List<ArmorItem>>() {{
        // hats

        List<ArmorItem> hats = new ArrayList<>();

        hats.add(new ArmorItem("leather_helmet", 0, ""));

        hats.addAll(BukkitUtil.CHATCOLOR_MAP.keySet().stream()
            .map(color -> new ArmorItem("leather_helmet", 0, "{'display' : { 'color' : '" + color.name() + "'}}"))
            .collect(Collectors.toList()));

        hats.addAll(Arrays.asList(
            new ArmorItem("chainmail_helmet", 0, ""),
            new ArmorItem("iron_helmet", 0, ""),
            new ArmorItem("gold_helmet", 0, ""),
            new ArmorItem("diamond_helmet", 0, ""),
            new ArmorItem("glass", 0, "{'display': {'name' : 'items.space'}, 'enchantments' : [{'name': 'oxygen', 'level': 2}]}"),
            new ArmorItem("pumpkin", 0, ""),
            new ArmorItem("jack_o_lantern", 0, ""),
            new ArmorItem("glowstone", 0, "")
        ));

        put(HAT, hats);

        // chest

        List<ArmorItem> chests = new ArrayList<>();

        chests.add(new ArmorItem("leather_chestplate", 0, ""));

        chests.addAll(BukkitUtil.CHATCOLOR_MAP.keySet().stream()
            .map(color -> new ArmorItem("leather_chestplate", 0, "{'display' : { 'color' : '" + color.name() + "'}}"))
            .collect(Collectors.toList()));

        chests.addAll(Arrays.asList(
            new ArmorItem("chainmail_chestplate", 0, ""),
            new ArmorItem("iron_chestplate", 0, ""),
            new ArmorItem("gold_chestplate", 0, ""),
            new ArmorItem("diamond_chestplate", 0, "")
        ));

        put(CHEST, chests);

        // pants

        List<ArmorItem> pants = new ArrayList<>();

        pants.add(new ArmorItem("leather_leggings", 0, ""));

        pants.addAll(BukkitUtil.CHATCOLOR_MAP.keySet().stream()
            .map(color -> new ArmorItem("leather_leggings", 0, "{'display' : { 'color' : '" + color.name() + "'}}"))
            .collect(Collectors.toList()));

        pants.addAll(Arrays.asList(
            new ArmorItem("chainmail_leggings", 0, ""),
            new ArmorItem("iron_leggings", 0, ""),
            new ArmorItem("gold_leggings", 0, ""),
            new ArmorItem("diamond_leggings", 0, "")
        ));

        put(PANTS, pants);

        // boots

        List<ArmorItem> boots = new ArrayList<>();

        boots.add(new ArmorItem("leather_boots", 0, ""));

        boots.addAll(BukkitUtil.CHATCOLOR_MAP.keySet().stream()
            .map(color -> new ArmorItem("leather_boots", 0, "{'display' : { 'color' : '" + color.name() + "'}}"))
            .collect(Collectors.toList()));

        boots.addAll(Arrays.asList(
            new ArmorItem("chainmail_boots", 0, ""),
            new ArmorItem("iron_boots", 0, ""),
            new ArmorItem("gold_boots", 0, ""),
            new ArmorItem("diamond_boots", 0, "")//,
            //new ArmorItem("diamond_boots", 0, "{'enchantments' : [{'name': 'protection_fall', 'level': 2}]}")
        ));

        put(BOOTS, boots);
    }};
}
