/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.servermenu.views;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.year4000.servermenu.Commons;
import net.year4000.servermenu.InventoryGUI;
import net.year4000.servermenu.ServerMenu;
import net.year4000.servermenu.gui.MapNodesGUI;
import net.year4000.servermenu.locales.Locales;
import net.year4000.utilities.sdk.HttpConnection;
import net.year4000.utilities.sdk.HttpFetcher;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.year4000.utilities.MessageUtil.replaceColors;

@AllArgsConstructor
public class GenerateView implements IconView {
    protected static final Material MATERIAL = Material.GLOWSTONE;
    protected static final String ID = "minecraft:glowstone"; // todo for future conversion to sponge

    /** The locale to display to the players */
    private Locale locale;
    /** The MapNodesGUI to change stage of generation */
    private MapNodesGUI menu;
    /** The group to generate */
    private String serverGroup;
    /** The stage of this view */
    private Stage stage;

    @Override
    public ItemStack make() {
        ItemStack item = new ItemStack(Material.GLOWSTONE);

        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        // Title
        meta.setDisplayName(replaceColors("&a&l" + Locales.SERVER_GENERATE_TITLE.translate(locale)));
        lore.add("");

        // Lore Description
        if (stage == Stage.GENERATING) {
            lore.add(Locales.SERVER_GENERATE_GENERATING.translate(locale));
        }
        else {
            String description = Locales.SERVER_GENERATE_DESCRIPTION.translate(locale);
            String[] descriptionLines = Commons.splitIntoLine(description, 30);
            for (String descriptionLine : descriptionLines) {
                lore.add(replaceColors(descriptionLine));
            }

            lore.add("");
            lore.add(Locales.SERVER_GENERATE_NORMAL.translate(locale));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void action(Locale locale, Player player, InventoryGUI gui) {
        String url = ServerMenu.api.api().addPath("nodes").addPath("generate").addPath(serverGroup).build();
        HttpFetcher.post(url, null, JsonObject.class, (d, e) -> {
            menu.setGenerating(true);

            // Let the player know what happen in chat
            if (e == null) {
                player.sendMessage(Locales.SERVER_PLAYER_GENERATE.translate(player));
            }
            else {
                player.sendMessage(Locales.SERVER_PLAYER_GENERATING.translate(player));
            }
        });
    }

    public enum Stage {
        NORMAL,
        GENERATING,
        ;
    }
}