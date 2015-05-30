package net.year4000.servermenu.views;

import lombok.AllArgsConstructor;
import net.year4000.servermenu.InventoryGUI;
import net.year4000.servermenu.locales.Locales;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class GenerateView implements IconView {
    protected static final Material MATERIAL = Material.GLOWSTONE;
    protected static final String ID = "minecraft:glowstone"; // todo for future conversion to sponge

    /** The locale to display to the players */
    private String locale;
    /** The group to generate */
    private String serverGroup;
    /** The stage of this view */
    private Stage stage;

    @Override
    public ItemStack make() {
        ItemStack item = new ItemStack(Material.AIR);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        // Title
        meta.setDisplayName(Locales.SERVER_GENERATE_TITLE.translate(locale));
        lore.add("");

        // Lore Description
        if (stage == Stage.GENERATING) {
            lore.add(Locales.SERVER_GENERATE_GENERATING.translate(locale));
            // todo apply exchange effect via nms hacking
        }
        else {
            lore.add(Locales.SERVER_GENERATE_DESCRIPTION.translate(locale));
            lore.add("");
            lore.add(Locales.SERVER_GENERATE_NORMAL.translate(locale));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void action(Player player, InventoryGUI gui, IconView view) {
        // todo call the api node generate route
    }

    public enum Stage {
        NORMAL,
        GENERATING,
        ;
    }
}