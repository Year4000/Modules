package net.year4000.servermenu.gui;

import lombok.ToString;
import net.year4000.servermenu.InventoryGUI;
import net.year4000.servermenu.ServerMenu;
import net.year4000.servermenu.Settings;
import net.year4000.servermenu.locales.MessageFactory;
import net.year4000.servermenu.views.CloseView;
import net.year4000.servermenu.views.IconView;
import net.year4000.servermenu.views.MenuIconView;
import net.year4000.utilities.MessageUtil;
import net.year4000.utilities.sdk.routes.players.PlayerCountJson;
import net.year4000.utilities.sdk.routes.players.PlayerCountRoute;
import org.bukkit.Material;

import java.util.Collection;
import java.util.Locale;

@ToString
public class MainMenuGUI extends AbstractGUI {
    private static String YEAR4000 = MessageUtil.replaceColors("&3[&bYear4000&3]");
    private PlayerCountRoute playerCountRoute;
    private String region;
    private String regionFormatted;
    private Settings.Menu[] menuViews;
    private int rows;

    /** Set up the MainMenu */
    public MainMenuGUI(String region, Settings.Menu[] menuViews, Collection<Locale> locales) {
        this.menuViews = menuViews;
        this.region = region;
        this.regionFormatted = ServerMenu.formatRegion(region);

        // Generate the inventory locales
        rows = menuViews.length > 0 && menuViews.length < 9 ? 1 : (int) Math.ceil(menuViews.length / 9);
        for (Locale locale : locales) {
            InventoryGUI inventoryGUI = create(rows);
            menus.put(locale, inventoryGUI);
        }
    }

    /** Calls the route and store a copy of it for us */
    @Override
    public void run() {
        try {
            playerCountRoute = ServerMenu.api.getPlayerCount();
        }
        catch (Exception e) {
            playerCountRoute = null;
        }

        menus.forEach((locale, gui) -> {
            gui.setIcons(generate(locale));
            gui.populate();
        });
    }

    @Override
    public IconView[][] generate(Locale locale) {
        IconView[][] views = new IconView[rows][InventoryGUI.COLS];
        int counter = 0;
        MenuIconView.State state = MenuIconView.State.TOP_MENU;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < ((y == 0) ? InventoryGUI.COLS - 1 : InventoryGUI.COLS); x++) {
                if (counter == menuViews.length) return views;

                // Create the game icons
                Settings.Menu menu = menuViews[counter++];
                // Menu Settings from Config
                Material material = Material.valueOf(menu.getIcon().toUpperCase());
                String name = MessageUtil.replaceColors("&6&l" + menu.getName());
                String group = region + menu.getGroupSuffix();
                String descriptionKey = "games." + menu.getName().toLowerCase().replaceAll(" ", "-") + ".description";
                String description = new MessageFactory.Message(locale.toString()).get(descriptionKey);
                // API stats
                PlayerCountJson.Count count = playerCountRoute == null ? null : playerCountRoute.getGroupPlayerCount(group);
                int servers = 0; // todo add this
                views[y][x] = new MenuIconView(locale, material, name, group, description, count, servers, state);
            }
        }

        views[0][8] = new CloseView(locale);

        return views;
    }

    /** Create the inventory */
    private InventoryGUI create(int rows) {
        String spaces = "             ";
        String title = spaces + YEAR4000;
        return new InventoryGUI(title, rows);
    }
}
