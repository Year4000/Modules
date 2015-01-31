package net.year4000.hubitems.items;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.year4000.hubitems.HubItems;
import net.year4000.hubitems.ItemActor;
import net.year4000.hubitems.messages.Message;
import net.year4000.hubitems.utils.Common;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class FunItemManager {
    private static FunItemManager inst;
    private List<FunItemInfo> itemInfo = new ArrayList<>();
    @Getter
    private Set<Material> itemMaterials = new HashSet<>();

    public static FunItemManager get() {
        if (inst == null) {
            inst = new FunItemManager();
        }

        return inst;
    }

    public FunItemInfo getItemInfo(Player player, String title) {
        return itemInfo.stream()
            .filter(i -> MessageUtil.stripColors(new Message(player).get(i.name())).equals(MessageUtil.stripColors(title)))
            .collect(Collectors.toList()).get(0);
    }


    /** Load the item info so we can manage it */
    public FunItemInfo loadItem(Class<? extends FunItem> item) {
        FunItemInfo info = item.getAnnotation(FunItemInfo.class);

        if (info == null) {
            HubItems.debug("Item info is null: " + item.getClass().getName());
        }
        else {
            HubItems.debug("Adding item: " + info.name());
            itemInfo.add(info);
            itemMaterials.add(info.icon());
        }

        return info;
    }

    /** Generate the item template */
    public ItemStack makeItem(Player player, FunItemInfo item) {
        Message locale = new Message(player);
        ItemStack stack = new ItemStack(item.icon());
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(MessageUtil.replaceColors("&a&l" + locale.get(item.name())));
        meta.setLore(new ArrayList<String>() {{
            // passive
            if (item.passive().isPassive()) {
                add(locale.get("mana.passive"));
            }
            // mana cost
            else {
                add(locale.get("mana.cost", Common.manaConverter(item.mana())));
            }

            // description
            for (String line : Common.loreDescription(locale.get(item.description()))) {
                add(MessageUtil.replaceColors("&5&o" + line));
            }

            // permission if needed
            if (!isVIP(player, item.permission())) {
                add("");
                for (String string : Common.loreDescription(locale.get(item.permission()[item.permission().length - 1]))) {
                    add(MessageUtil.replaceColors("&6" + string));
                }
            }
        }});

        stack.setItemMeta(meta);
        //HubItems.debug(stack.toString());
        return stack;
    }

    /** Load the items to the player's inventory for the first time */
    public ItemStack[] loadItems(Player player) {
        Message locale = new Message(player);
        ItemStack[] items = new ItemStack[itemInfo.size() + 9];
        //HubItems.debug(itemInfo.size()+"");

        for (int i = 9; i < itemInfo.size() + 9; i++) {
            FunItemInfo info = itemInfo.get(i - 9);
            items[i] = makeItem(player, info);

            if (!isVIP(player, info.permission())) continue;

            boolean pendingGlow = info.passive().isActive();

            if (!info.passive().isPassive()) {
                ItemMeta meta = items[i].getItemMeta();
                List<String> lore = meta.getLore();

                if (ItemActor.get(player).getCurrentItem() == info) {
                    lore.addAll(Arrays.asList("", locale.get("mana.selected")));
                    pendingGlow = true; // shimmer effect
                }
                else {
                    lore.addAll(Arrays.asList("", locale.get("mana.select")));
                }

                meta.setLore(lore);
                items[i].setItemMeta(meta);
            }

            if (pendingGlow) {
                items[i] = Common.addGlow(items[i]);
            }
        }

        return items;
    }

    /** Is the selected player a VIP */
    public static boolean isVIP(Player player, String... VIPS) {
        for (String permission : VIPS) {
            if (player.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }
}
