package net.year4000.dressup;

import com.ewized.utilities.bukkit.util.ItemUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class ArmorItem {
    private String name;
    private int damage;
    private String nbt;

    public ItemStack makeItem() {
        ItemStack item = ItemUtil.makeItem(name, 1, (short) damage);

        if (!nbt.equalsIgnoreCase("")) {
            item.setItemMeta(ItemUtil.addMeta(item, nbt));
        }

        return item;
    }
}
