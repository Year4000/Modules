package net.year4000.dressup;

import com.ewized.utilities.bukkit.util.BukkitUtil;
import lombok.Getter;
import net.year4000.dressup.message.Message;
import net.year4000.dressup.message.MessageManager;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.core.module.ModuleInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ModuleInfo(
    name = "DressUp",
    version = "1.2",
    description = "Change your armor",
    authors = {"Year4000"}
)
@ModuleListeners({DressListener.class})
public class DressUp extends BukkitModule {
    @Getter
    private static DressUp inst;
    private static Map<Locale, Inventory> hats = new HashMap<>();
    private static Map<Locale, Inventory> shirts = new HashMap<>();
    private static Map<Locale, Inventory> pants = new HashMap<>();
    private static Map<Locale, Inventory> boots = new HashMap<>();

    @Override
    public void load() {
        inst = this;
    }

    @Override
    public void enable() {
        MessageManager.get().getLocales().forEach((locale, file) -> {
            hats.put(locale, makeInv(Settings.HAT, locale));
            shirts.put(locale, makeInv(Settings.CHEST, locale));
            pants.put(locale, makeInv(Settings.PANTS, locale));
            boots.put(locale, makeInv(Settings.BOOTS, locale));
        });
    }

    /** Make the inventory based on the locale message */
    private Inventory makeInv(String list, Locale locale) {
        List<ArmorItem> items = Settings.get().getItems().get(list);
        Inventory inv = Bukkit.createInventory(null, BukkitUtil.invBase(items.size()), new Message(locale).get("inv." + list));


        ItemStack[] content = new ItemStack[items.size()];

        for (int i = 0; i < items.size(); i++) {
            content[i] = items.get(i).makeItem();
        }

        inv.setContents(content);

        return inv;
    }

    public static Inventory openHat(Player player) {
        return hats.get(new Locale(player.getLocale()));
    }

    public static Inventory openChest(Player player) {
        return shirts.get(new Locale(player.getLocale()));
    }

    public static Inventory openPants(Player player) {
        return pants.get(new Locale(player.getLocale()));
    }

    public static Inventory openBoots(Player player) {
        return boots.get(new Locale(player.getLocale()));
    }
}
