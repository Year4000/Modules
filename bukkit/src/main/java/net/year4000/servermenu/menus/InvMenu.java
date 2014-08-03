package net.year4000.servermenu.menus;

import com.ewized.utilities.bukkit.util.BukkitUtil;
import com.ewized.utilities.bukkit.util.ItemUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import com.google.common.collect.ImmutableSet;
import lombok.Data;
import net.year4000.servermenu.BungeeSender;
import net.year4000.servermenu.Settings;
import net.year4000.servermenu.message.Message;
import net.year4000.servermenu.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class InvMenu {
    private static final Set<Material> ITEMS = ImmutableSet.of(Material.ARROW, Material.CHAINMAIL_HELMET, Material.BOW, Material.GLOWSTONE, Material.MINECART, Material.LAVA_BUCKET, Material.POTION, Material.PORTAL);
    private final String menu;
    private final String menuDisplay;
    private final String[] group;
    private final boolean players;
    private final boolean motd;
    private Map<Integer, Map<Locale, ItemStack[]>> pages;
    private Map<Locale, Inventory> views = new HashMap<>();
    private Collection<ServerJson> ping = APIManager.getServers();
    private List<ServerJson> api;
    private final int serversCount;

    public InvMenu(boolean players, boolean motd, String menu, String... group) {
        api = ping.stream().filter(s -> s.getGroup().getName().equals(menu) && !s.getName().startsWith(".")).collect(Collectors.toList());
        serversCount = api.size();
        this.players = players;
        this.motd = motd;
        this.menu = menu;
        this.group = group;

        if (serversCount > 0) {
            this.menuDisplay = api.get(0).getGroup().getDisplay();
        }
        else {
            this.menuDisplay = menu;
        }

        // construct defaults
        MessageManager.get().getLocales().keySet().forEach(code -> views.put(code, makeMenuInventory()));
    }

    public void updateServers() {
        views.forEach(this::updateServers);
    }

    public Inventory openMenu(String code) {
        return views.get(new Locale(MessageManager.get().isLocale(code) ? code : Message.DEFAULT_LOCALE));
    }

    private Inventory makeMenuInventory() {
        boolean oneGroup = group.length > 1;
        int invSize = BukkitUtil.invBase(serversCount + (oneGroup ? 18 : 9));
        return Bukkit.createInventory(null, invSize, MessageUtil.replaceColors("&8&l" + menuDisplay));
    }

    public void updateServers(Locale locale, Inventory menu) {
        if (menu.getViewers().size() == 0) return;
        ping = APIManager.getServers();
        api = ping.stream().filter(s -> s.getGroup().getName().equals(this.menu) && !s.getName().startsWith(".")).collect(Collectors.toList());

        String code = locale.toString();
        boolean oneGroup = group.length > 1;
        int invSize = BukkitUtil.invBase(serversCount + (oneGroup ? 18 : 9));
        ItemStack[] items = new ItemStack[invSize];

        // Menu Bar
        if (oneGroup) {
            int count = -1;

            for (String item : group) {
                ServerJson.Group group = APIManager.getGroups().stream().filter(g -> g.getName().equals(item)).findAny().get();
                items[++count] = createItemBar(count, group, code, (int) ping.stream().filter(s -> s.getGroup().getName().equals(item)).count());
            }

            // Hub Icon
            if (Settings.get().isHub()) {
                InvMenu hubs =  new InvMenu(true, false, Settings.get().getHubGroup());
                ServerJson.Group group = APIManager.getGroups().stream().filter(g -> g.getName().equals(Settings.get().getHubGroup())).findAny().get();

                items[8] = createItemBar(7, group, code, hubs.serversCount);
                items[8].removeEnchantment(Enchantment.LURE);
            }
        }

        // Servers
        int servers = oneGroup ? 8 : -1;

        for (ServerJson server : api) {
            items[++servers] = serverItem(code, server);
        }

        items[invSize - 5] = ItemUtil.makeItem("redstone_block", "{'display':{'name':'" + new Message(code).get("menu.close") + "'}}");

        menu.setContents(items);
    }

    /** Create the item in the menu bar */
    private ItemStack createItemBar(int count, ServerJson.Group menu, String code, int servers) {
        Message locale = new Message(code);
        ItemStack item = ItemUtil.makeItem(ITEMS.toArray(new Material[ITEMS.size()])[count].name());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(MessageUtil.replaceColors("&a" + menu.getDisplay()));
        String menuName = this.menu;
        meta.setLore(new ArrayList<String>() {{
            add(locale.get("menu.servers", servers));

            if (!menu.getName().equals(menuName)) {
                add("");
                add(locale.get("menu.click", menu.getDisplay()));
            }
        }});

        // glow
        if (this.menu.equals(menu.getName())) {
            meta.addEnchant(Enchantment.LURE, 1, true);
        }

        item.setItemMeta(meta);
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
            item = ItemUtil.makeItem(Material.STAINED_CLAY.name(), number, self ? (short) 5 : (short) 13);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(MessageUtil.replaceColors("&b" + server.getName()));
            meta.setLore(new ArrayList<String>() {{
                // Message of the day.
                if (motd) {
                    add(MessageUtil.replaceColors(server.getStatus().getDescription()));
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
            item = ItemUtil.makeItem(Material.STAINED_CLAY.name(), number, self ? (short) 6 : (short) 14);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(MessageUtil.replaceColors("&b" + server.getName()));
            meta.setLore(Arrays.asList(locale.get("server.offline")));
            item.setItemMeta(meta);
        }

        return item;
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
