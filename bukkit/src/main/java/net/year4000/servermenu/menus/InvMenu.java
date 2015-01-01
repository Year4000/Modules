package net.year4000.servermenu.menus;

import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableSet;
import lombok.Data;
import net.year4000.servermenu.BungeeSender;
import net.year4000.servermenu.Common;
import net.year4000.servermenu.ServerMenu;
import net.year4000.servermenu.Settings;
import net.year4000.servermenu.message.Message;
import net.year4000.servermenu.message.MessageManager;
import net.year4000.utilities.bukkit.BukkitUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Data
public class InvMenu {
    private static final Set<Material> ITEMS = ImmutableSet.of(Material.ARROW, Material.CHAINMAIL_HELMET, Material.BOW, Material.GLOWSTONE, Material.MINECART, Material.LAVA_BUCKET, Material.POTION, Material.EYE_OF_ENDER);
    private final String menu;
    private final String menuDisplay;
    private final String[] group;
    private final boolean players;
    private final boolean motd;
    private final MenuManager manager;
    private Map<Locale, Inventory> views = new HashMap<>();
    private int serversCount;

    public InvMenu(MenuManager manager, boolean players, boolean motd, String menu, String... group) {
        this.manager = manager;
        this.players = players;
        this.motd = motd;
        this.menu = menu;
        this.group = group;

        serversCount = getServers().size();

        // The menu display / title
        String name = menu;
        try {
            name = serversCount > 0 ? getServers().get(0).getGroup().getDisplay() : menu;
        } finally {
            this.menuDisplay = name;
        }

        createMenus();
    }

    // Util Methods //

    /** Create menus */
    public void createMenus() {
        // create the menus for each locale
        String title = Ascii.truncate(MessageUtil.replaceColors("&8&l" + menuDisplay), 32, "...");

        views.clear();
        MessageManager.get().getLocales().keySet().forEach(code -> {
            // can not abstract inventory or the code will think its the same inv
            Inventory inv = Bukkit.createInventory(null, menuSize(), title);
            views.put(code, inv);
        });
    }

    /** Get servers that are specific to this list */
    private List<ServerJson> getServers() {
        return getServers(false);
    }

    /** Get servers that are specific to this list */
    private List<ServerJson> getServers(boolean showHidden) {
        Predicate<? super ServerJson> hide = s -> s.getGroup().getName().equals(menu) && !s.isHidden();
        Predicate<? super ServerJson> all = s -> s.getGroup().getName().equals(menu);

        return manager.getServers().stream().filter(showHidden ? all : hide).collect(Collectors.toList());
    }

    /** Open the inventory that follows the locale code */
    public Inventory openMenu(String code) {
        return views.get(new Locale(MessageManager.get().isLocale(code) ? code : Message.DEFAULT_LOCALE));
    }

    /** The menu size to use for this view which includes top bar and close button */
    private int menuSize() {
        boolean oneGroup = group.length > 1;
        boolean shortMenu = serversCount < 9 && !oneGroup;
        return BukkitUtil.invBase(shortMenu ? serversCount : serversCount + (oneGroup ? 18 : 9));
    }

    /** Is server size is not the same as the last one */
    public boolean needNewInventory() {
        return getServers().size() != serversCount;
    }

    // Update Servers //

    /** Regenerate all the menus as we need to regenerate the menu size */
    public void regenerateMenuViews() {
        // get viewers
        Map<HumanEntity, Locale> pendingUpdate = new HashMap<>();
        views.forEach((locale, inv) -> inv.getViewers().forEach(h -> pendingUpdate.put(h ,locale)));

        // update menus
        serversCount = getServers().size();
        if (serversCount == 0) {
            pendingUpdate.keySet().forEach(HumanEntity::closeInventory);
            MenuManager.get().updateServers();
        }
        else {
            createMenus();
            // viewers get new views
            pendingUpdate.forEach((h, l) -> h.openInventory(openMenu(l.toString())));
        }
    }

    /** Update the inventory of the servers */
    public void updateServers() {
        views.forEach(this::updateServers);
    }

    public void updateServers(Locale locale, Inventory menu) {
        String code = locale.toString();
        boolean oneGroup = group.length > 1;
        boolean shortMenu = serversCount < 9 && !oneGroup;
        int invSize = menuSize();
        ItemStack[] items = new ItemStack[invSize];

        // Menu Bar
        if (oneGroup) {
            int count = -1;

            for (String item : group) {
                if (!manager.getGroups().stream().map(ServerJson.Group::getName).collect(Collectors.toSet()).contains(item)) continue;

                ServerJson.Group group = manager.getGroups().stream().filter(g -> g.getName().equals(item)).findAny().get();
                items[++count] = createItemBar(count, group, code, (int) manager.getServers().stream().filter(s -> s.getGroup().getName().equals(item)).count());
            }

            // Hub Icon
            if (Settings.get().isHub()) {
                InvMenu hubs = new InvMenu(manager, true, false, Settings.get().getHubGroup());
                ServerJson.Group group = manager.getGroups().stream().filter(g -> g.getName().equals(Settings.get().getHubGroup())).findAny().get();

                items[8] = createItemBar(ITEMS.size() - 1, group, code, hubs.serversCount);
            }
        }

        // Servers
        int servers = oneGroup ? 8 : -1;
        boolean mapNodes = getServers().stream()
            .filter(s -> s.getStatus() != null)
            .filter(s -> s.getStatus().getDescription().contains("|"))
            .count() > 0L;

        if (mapNodes) {
            String[] targets = new String[]{"ENDING", "WAITING", "STARTING", "PLAYING", null};

            for (int i = 0; i < targets.length ; i++) {
                final int j = i;
                List<ServerJson> sortedServers;

                if (targets[j] == null) {
                    sortedServers = getServers().stream()
                        .filter(s -> s.getStatus() == null)
                        .collect(Collectors.toList());
                }
                else {
                    sortedServers = getServers().stream()
                        .filter(s -> s.getStatus() != null)
                        .filter(s -> s.getStatus().getDescription().contains(targets[j]))
                        .collect(Collectors.toList());
                }

                for (ServerJson server : sortedServers) {
                    items[++servers] = serverItem(code, server);
                }
            }
        }
        else {
            for (ServerJson server : getServers()) {
                items[++servers] = serverItem(code, server);
            }
        }


        items[shortMenu ? 8 : invSize - 5] = ServerMenu.closeButton(new Locale(code));

        menu.setContents(items);
    }

    // Create the menu items //

    /** Create the item in the menu bar */
    private ItemStack createItemBar(int count, ServerJson.Group menu, String code, int servers) {
        Message locale = new Message(code);
        ItemStack item = ItemUtil.makeItem(ITEMS.toArray(new Material[ITEMS.size()])[count].name());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(MessageUtil.replaceColors("&a&l" + menu.getDisplay()));
        String menuName = this.menu;
        meta.setLore(new ArrayList<String>() {{
            add(locale.get("menu.servers", servers));

            if (!menu.getName().equals(menuName)) {
                add("");
                add(locale.get("menu.click"));
            }
        }});

        item.setItemMeta(meta);

        // glow
        if (this.menu.equals(menu.getName())) {
            return Common.addGlow(item);
        }

        return item;
    }

    /** Create the server icon */
    private ItemStack serverItem(String code, ServerJson server) {
        ItemStack item;
        Message locale = new Message(code);

        // Server is itself
        boolean self = server.getName().equals(BungeeSender.getCurrentServer());

        if (server.getStatus() != null) {
            int number = findNumber(server.getName());
            item = ItemUtil.makeItem(Material.STAINED_CLAY.name(), number, getMapNodesStatus(server.getStatus().getDescription()));

            if (self) {
                item = Common.addGlow(item);
            }

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(MessageUtil.replaceColors("&b&l" + server.getName()));
            meta.setLore(new ArrayList<String>() {{
                // Message of the day.
                if (motd) {
                    add(MessageUtil.replaceColors(Ascii.truncate(server.getStatus().getDescription(), 45, "&7...")));
                }

                // Player count.
                if (players) {
                    add(MessageUtil.message(
                        "&a%s&7/&6%s",
                        server.getStatus().getPlayers().getOnline(),
                        server.getStatus().getPlayers().getMax()
                    ));
                }

                // Status ect
                add(locale.get("server.online"));

                if (!self) {
                    add("");
                    add(locale.get("server.click"));
                }
            }});
            item.setItemMeta(meta);
        }
        //offline
        else {
            int number = findNumber(server.getName());
            item = ItemUtil.makeItem(Material.STAINED_CLAY.name(), -number, (short) 14);

            if (self) {
                item = Common.addGlow(item);
            }

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(MessageUtil.replaceColors("&b&l" + server.getName()));
            meta.setLore(Arrays.asList(locale.get("server.offline")));
            item.setItemMeta(meta);
        }

        return item;
    }

    private short getMapNodesStatus(String motd) {
        if (motd.contains("WAITING")) {
            return 4;
        }
        else if (motd.contains("STARTING")) {
            return 13;
        }
        else if (motd.contains("PLAYING")) {
            return 5;
        }
        else if (motd.contains("ENDING")) {
            return 6;
        }

        return 13;
    }

    /** Find the server's number */
    private int findNumber(String name) {
        int number = 1;

        for (String part : name.split(" ")) {
            try {
                number = Integer.parseInt(part) < 1 ? 1 : Integer.parseInt(part);
                break;
            } catch (Exception e) {
                // not a valid number skip
            }
        }

        return number;
    }
}
