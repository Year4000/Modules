package net.year4000.servermenu.views;

import lombok.AllArgsConstructor;
import net.year4000.servermenu.InventoryGUI;
import net.year4000.servermenu.locales.Locales;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

@AllArgsConstructor
public class CloseView implements IconView {
    private Locale locale;

    @Override
    public ItemStack make() {
        ItemStack item = ItemUtil.makeItem("redstone_block");
        ItemMeta meta = item.getItemMeta();

        String title = Locales.MENU_CLOSE.translate(locale);
        meta.setDisplayName(title);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void action(Player player, InventoryGUI gui, IconView view) {
        if (view instanceof CloseView) {
            player.closeInventory();
        }
    }
}
