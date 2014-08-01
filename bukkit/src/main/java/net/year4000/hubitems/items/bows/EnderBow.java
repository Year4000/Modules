package net.year4000.hubitems.items.bows;

import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import org.bukkit.Material;

@FunItemInfo(
    name = "enderbow.name",
    icon = Material.BOW,
    description = "enderbow.description",
    permission = {"pi" , "enderbow.permission"},
    mana = 0.05F
)
public class EnderBow extends FunItem {
}
