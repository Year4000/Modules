package net.year4000.servermenu.views;

import lombok.AllArgsConstructor;
import net.year4000.hubitems.utils.Common;
import net.year4000.servermenu.BungeeConnector;
import net.year4000.servermenu.InventoryGUI;
import net.year4000.servermenu.locales.Locales;
import net.year4000.utilities.sdk.routes.players.PlayerCountJson;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.year4000.utilities.bukkit.MessageUtil.replaceColors;

public class ServerView implements IconView {
    protected static final Material MATERIAL = Material.STAINED_CLAY;
    protected static final String ID = "minecraft:stained_hardened_clay"; // todo for future conversion to sponge

    /** The locale code to translate the strings */
    protected Locale locale;
    /** This display name of the server */
    protected String serverName;
    /** The count of players on this server */
    protected PlayerCountJson.Count count;
    /** The state of this server view for data value of the clay block */
    protected State state;

    /** The outside classes can create an instance of this object */
    public ServerView(Locale locale, String serverName, PlayerCountJson.Count count, State state) {
        this(locale, serverName, count);
        this.state = state;
    }

    /** Internal to allow subclasses to create the instance */
    protected ServerView(Locale locale, String serverName, PlayerCountJson.Count count) {
        this.locale = locale;
        this.serverName = serverName;
        this.count = count;
    }

    @Override
    public ItemStack make() {
        boolean same = BungeeConnector.getCurrentServer().equals(serverName);
        ItemStack item = new ItemStack(MATERIAL, getCount(), (short) state.data);

        // Add a glow to item
        if (same) {
            item = Common.addGlow(item);
        }

        ItemMeta meta = item.getItemMeta();

        // Name
        meta.setDisplayName(replaceColors("&a&l" + serverName));

        // Lore
        List<String> lore = new ArrayList<>();

        if (state == State.ONLINE) {
            String online = String.valueOf(count.getOnline());
            String max = String.valueOf(count.getOnline());
            lore.add(Locales.MENU_PLAYERS.translate(locale, online, max));
            lore.add(Locales.SERVER_ONLINE.translate(locale));

            // If the server is the server its on
            if (!same) {
                lore.add("");
                lore.add(Locales.SERVER_CLICK.translate(locale));
            }
            else {
                Common.addGlow(item);
            }
        }
        else {
            lore.add(Locales.SERVER_OFFLINE.translate(locale));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /** Get the current count of the server */
    protected int getCount() {
        String[] parts = serverName.split(" ");
        int id = 1;

        for (int i = parts.length - 1; i >= 0; i--) {
            try {
                String part = parts[i];
                id = Integer.valueOf(part);
                return id;
            }
            catch (NumberFormatException e) {
                id = 0;
            }
        }

        return id;
    }

    @Override
    public void action(Player player, InventoryGUI gui, IconView view) {
        ServerView serverView = (ServerView) view;
        String server = serverView.serverName;
        player.sendMessage(Locales.SERVER_CONNECT.translate(player, server));
        new BungeeConnector(server).send(player);
    }

    @AllArgsConstructor
    public enum State {
        ONLINE(13, "green"),
        OFFLINE(14, "red"),;

        private int data;
        private String color;
    }
}