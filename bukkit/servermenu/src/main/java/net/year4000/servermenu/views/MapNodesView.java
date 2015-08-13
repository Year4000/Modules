/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.servermenu.views;

import com.google.common.base.Charsets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.year4000.servermenu.locales.Locales;
import net.year4000.utilities.sdk.routes.players.PlayerCountJson;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.year4000.utilities.MessageUtil.replaceColors;

public class MapNodesView extends ServerView implements IconView {
    protected String motd;
    /** The state of this server view for data value of the clay block */
    protected State state;

    /** Allow to create the instance of MapNodesView */
    public MapNodesView(Locale locale, String server, String motd, PlayerCountJson.Count count, State state) {
        super(locale, server, count);
        this.motd = new String(motd.getBytes(), Charsets.UTF_8);
        this.state = state;
    }

    @Override
    public ItemStack make() {
        ItemStack item = new ItemStack(MATERIAL, getCount(), (short) state.data);
        ItemMeta meta = item.getItemMeta();

        // Name
        meta.setDisplayName(replaceColors("&b&l" + serverName));

        // Lore
        List<String> lore = new ArrayList<>();

        if (state != State.OFFLINE) {
            lore.add(replaceColors(motd));
            lore.add(replaceColors("&a" + count.getOnline() + "&7/&6" + count.getMax()));
            lore.add(Locales.SERVER_ONLINE.translate(locale));
            lore.add("");
            lore.add(Locales.SERVER_CLICK.translate(locale));
        }
        else {
            lore.add(Locales.SERVER_OFFLINE.translate(locale));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @AllArgsConstructor
    public enum State {
        WAITING(4, "yellow", 1),
        STARTING(5, "green", 2),
        PLAYING(5, "lime", 3),
        ENDING(1, "orange", 4),
        ENDED(1, "orange", 5),
        OFFLINE(14, "red", 6);

        private int data;
        private String color;
        @Getter
        private int sort;

        /** Find the state of the current string */
        public static State findState(String string) {
            String find = string.toUpperCase();

            if (find.contains(WAITING.name())) {
                return WAITING;
            }
            else if (find.contains(STARTING.name())) {
                return STARTING;
            }
            else if (find.contains(PLAYING.name())) {
                return PLAYING;
            }
            else if (find.contains(ENDING.name())) {
                return ENDING;
            }
            else if (find.contains(ENDED.name())) {
                return ENDED;
            }
            else {
                return OFFLINE;
            }
        }
    }
}
