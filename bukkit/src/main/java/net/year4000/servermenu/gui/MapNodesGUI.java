package net.year4000.servermenu.gui;

import com.google.common.base.Preconditions;
import lombok.Setter;
import net.year4000.servermenu.Commons;
import net.year4000.servermenu.InventoryGUI;
import net.year4000.servermenu.ServerMenu;
import net.year4000.servermenu.Settings;
import net.year4000.servermenu.locales.MessageFactory;
import net.year4000.servermenu.views.*;
import net.year4000.utilities.URLBuilder;
import net.year4000.utilities.sdk.API;
import net.year4000.utilities.sdk.routes.players.PlayerCountJson;
import net.year4000.utilities.sdk.routes.servers.ServerJson;
import net.year4000.utilities.sdk.routes.servers.ServersRoute;
import org.bukkit.Material;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public class MapNodesGUI extends AbstractGUI {
    private static final int ROWS = 2; // todo allow more rows later
    private URLBuilder url;
    private ServersRoute route;
    private Settings.Menu menu;
    @Setter
    private boolean generating = false;

    /** Create this instance class with the display and group */
    public MapNodesGUI(Settings.Menu menu, Collection<Locale> locales) {
        this.menu = Preconditions.checkNotNull(menu);
        String group = "us" + Preconditions.checkNotNull(menu.getGroupSuffix());
        this.url = URLBuilder.builder(API.BASE_URL)
            .addPath("servers")
            .addQuery("group", group);

        // Create the locales for the menus
        for (Locale locale : locales) {
            // Generate with one row for the server generate
            InventoryGUI inventoryGUI = new InventoryGUI(menu.getName(), ROWS);
            menus.put(locale, inventoryGUI);
        }
    }


    @Override
    public IconView[][] generate(Locale locale) {
        IconView[][] icons = new IconView[ROWS][InventoryGUI.COLS];

        // Category Icon
        Material icon = Material.valueOf(menu.getIcon().toUpperCase());
        String descriptionKey = "games." + menu.getName().toLowerCase().replaceAll(" ", "-") + ".description";
        String description = new MessageFactory.Message(locale.toString()).get(descriptionKey);
        ServerJson[] servers = new ServerJson[0];
        Collection<ServerJson> collection;

        // Filter servers and sort them if any
        if (route != null) {
            collection = route.getServersCollection();
            servers = collection.stream()
                .filter(s -> s.getStatus() != null)
                .filter(s -> {
                    String motd = s.getStatus().getDescription();
                    MapNodesView.State state = MapNodesView.State.findState(motd);
                    return state != MapNodesView.State.OFFLINE;
                })
                .sorted((l, r) -> {
                    String leftMotd = l.getStatus().getDescription();
                    MapNodesView.State leftState = MapNodesView.State.findState(leftMotd);
                    String rightMotd = r.getStatus().getDescription();
                    MapNodesView.State rightState = MapNodesView.State.findState(rightMotd);
                    int rightCount = rightState.getSort(), leftCount = leftState.getSort();

                    // Sort order Waiting, Ending, Starting, Playing
                    return rightCount == leftCount ? 0 : rightCount > leftCount ? -1 : 1;
                })
                .collect(Collectors.toList())
                .toArray(new ServerJson[] {});
        }

        // Show generate server icon
        if (route == null || servers.length == 0) {
            GenerateView.Stage stage = generating ? GenerateView.Stage.GENERATING : GenerateView.Stage.NORMAL;
            icons[0][0] = new MenuIconView(locale, icon, menu.getName(), "us"+ menu.getGroupSuffix(), description, null, servers.length, MenuIconView.State.SUB_MENU);
            icons[0][8] = new CloseView(locale);
            icons[1][4] = new GenerateView(locale, this, "us" + menu.getGroupSuffix(), stage);
        }
        // Generate playable servers that are sorted
        else {
            icons[0][0] = new MenuIconView(locale, icon, menu.getName(), "us"+ menu.getGroupSuffix(), description, null, servers.length, MenuIconView.State.SUB_MENU);
            icons[0][8] = new CloseView(locale);

            int counter = -1;
            for (int x = 1; x < ROWS; x++) {
                for (int y = 0; y < InventoryGUI.COLS; y++) {
                    // Reached last server return the array
                    if (++counter == servers.length) return icons;

                    // Create the MapNodes view
                    ServerJson server = servers[counter];
                    String motd = server.getStatus().getDescription();
                    int online = server.getStatus().getPlayers().getOnline();
                    int max = server.getStatus().getPlayers().getMax();
                    PlayerCountJson.Count count = new PlayerCountJson.Count(online, max);
                    MapNodesView.State state = MapNodesView.State.findState(motd);
                    icons[x][y] = new MapNodesView(locale, server.getName(), motd, count, state);
                }
            }
        }

        return icons;
    }

    @Override
    public void run() {
        try {
            route = ServerMenu.api.getRoute(ServersRoute.class, API.SERVERS_TYPE, url);
        }
        catch (Exception e) {
            // e.printStackTrace();
            route = null;
        }

        menus.forEach((locale, gui) -> {
            gui.setIcons(generate(locale));
            gui.populate();
        });
    }
}
