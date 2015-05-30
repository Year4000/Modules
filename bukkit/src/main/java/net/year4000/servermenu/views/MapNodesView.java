package net.year4000.servermenu.views;

import lombok.AllArgsConstructor;
import net.year4000.servermenu.locales.Locales;
import net.year4000.utilities.sdk.routes.players.PlayerCountJson;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static net.year4000.utilities.MessageUtil.replaceColors;

public class MapNodesView extends ServerView implements IconView {
    protected String motd;
    /** The state of this server view for data value of the clay block */
    protected State state;

    /** Allow to create the instance of MapNodesView */
    public MapNodesView(String locale, String server, String motd, PlayerCountJson.Count count, State state) {
        super(locale, server, count);
        this.motd = motd;
        this.state = state;
    }

    @Override
    public ItemStack make() {
        ItemStack item = new ItemStack(MATERIAL, getCount(), (short) state.data);
        ItemMeta meta = item.getItemMeta();

        // Name
        meta.setDisplayName(replaceColors("&a&l" + serverName));

        // Lore
        List<String> lore = new ArrayList<>();

        if (state != State.OFFLINE) {
            lore.add(replaceColors(motd));
            String online = String.valueOf(count.getOnline());
            String max = String.valueOf(count.getMax());
            lore.add(Locales.MENU_PLAYERS.translate(locale, online, max));
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
        WAITING(4, "yellow"),
        STARTING(5, "green"),
        PLAYING(5, "lime"),
        ENDING(1, "orange"),
        OFFLINE(14, "red"),;

        private int data;
        private String color;
    }
}
