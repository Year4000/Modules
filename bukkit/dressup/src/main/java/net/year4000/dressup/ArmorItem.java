/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.dressup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.dressup.message.Message;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.config.Config;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ArmorItem extends Config {
    private String name;
    private int damage;
    private String nbt;

    public ItemStack makeItem(Locale locale) {
        ItemStack item = ItemUtil.makeItem(name, 1, (short) damage);
        Message select = new Message(locale);

        // item nbt
        if (!nbt.equalsIgnoreCase("")) {
            ItemMeta meta = ItemUtil.addMeta(item, nbt);

            // item translations
            if (meta.hasDisplayName()) {
                meta.setDisplayName(select.get(meta.getDisplayName()));
            }

            // module nbt
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add("");
            lore.add(select.get("inv.click"));
            meta.setLore(lore);

            item.setItemMeta(meta);
        }
        // items with no nbt
        else {
            ItemMeta meta = item.getItemMeta();

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(select.get("inv.click"));
            meta.setLore(lore);

            item.setItemMeta(meta);
        }

        return item;
    }
}
