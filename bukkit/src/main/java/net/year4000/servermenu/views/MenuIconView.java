package net.year4000.servermenu.views;

import lombok.AllArgsConstructor;
import net.year4000.servermenu.Commons;
import net.year4000.servermenu.InventoryGUI;
import net.year4000.servermenu.ServerMenu;
import net.year4000.servermenu.gui.AbstractGUI;
import net.year4000.servermenu.locales.Locales;
import net.year4000.utilities.MessageUtil;
import net.year4000.utilities.sdk.routes.players.PlayerCountJson;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static net.year4000.utilities.MessageUtil.replaceColors;

@AllArgsConstructor
public class MenuIconView implements IconView {
    private Locale locale;
    private Material material;
    private String name;
    private String id;
    private String description;
    private PlayerCountJson.Count count;
    private int servers;
    private State state;

    @Override
    public ItemStack make() {
        ItemStack item = new ItemStack(material);

        // Add glow is sub menu
        if (state == State.SUB_MENU) {
            item = Commons.addGlow(item);
        }

        ItemMeta meta = item.getItemMeta();

        // Name
        meta.setDisplayName(replaceColors("&a&l" + name));

        // Lore
        List<String> lore = new ArrayList<>();
        boolean emptyCount = count != null;
        boolean emptyServer = servers > 0;

        // What to show in top menu
        if (state == State.TOP_MENU) {
            lore.add("");
            String[] descriptionLines = Commons.splitIntoLine(description, 30);
            for (String descriptionLine : descriptionLines) {
                lore.add(replaceColors(descriptionLine));
            }

            // If count is empty do not show
            if (emptyCount || emptyServer) {
                lore.add("");
            }

            if (emptyCount) {
                lore.add(Locales.MENU_PLAYERS.translate(locale, count.getOnline(), count.getMax()));
            }

            if (emptyServer) {
                lore.add(Locales.MENU_SERVERS.translate(locale, servers));
            }

            lore.add("");
            lore.add(Locales.MENU_CLICK.translate(locale));
        }
        // What to show in submenu
        else if (state == State.SUB_MENU){
            // If count is empty do not show
            if (emptyCount) {
                lore.add(Locales.MENU_PLAYERS.translate(locale, count.getOnline(), count.getMax()));
            }

            if (emptyServer) {
                lore.add(Locales.MENU_SERVERS.translate(locale, servers));
            }

            if (emptyCount || emptyServer) {
                lore.add("");
            }

            lore.add(Locales.MENU_CLICK_SUB.translate(locale));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void action(Locale locale, Player player, InventoryGUI gui) {
        if (state == State.TOP_MENU) {
            player.sendMessage(Locales.MENU_OPEN.translate(locale, name));
            List<AbstractGUI> menu = ServerMenu.inst.getMenus().stream()
                .filter(m -> {
                    String title = m.getInventory(locale).getTitle();
                    return MessageUtil.stripColors(title).equals(MessageUtil.stripColors(name));
                })
                .collect(Collectors.toList());

            menu.get(0).openInventory(player);
        }
        else if (state == State.SUB_MENU) {
            player.sendMessage(Locales.MENU_OPEN.translate(locale, name));
            ServerMenu.inst.getMenus().get(0).openInventory(player);
        }
    }

    /** The state of this view */
    public enum State {
        TOP_MENU,
        SUB_MENU,
        ;
    }
}