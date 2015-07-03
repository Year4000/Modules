package net.year4000.hubitems.items.passive;

import net.year4000.hubitems.items.FunItem;
import net.year4000.hubitems.items.FunItemInfo;
import net.year4000.hubitems.items.PassiveState;
import org.bukkit.Material;

@FunItemInfo(
    name = "dress.name",
    icon = Material.CHAINMAIL_CHESTPLATE,
    description = "dress.description",
    permissionLocale = "dress.permission",
    permission = {"theta", "mu", "pi", "sigma", "phi", "delta", "omega"},
    passive = PassiveState.ALLWAYS_ON
)
public class Dress extends FunItem {
    /* This items lets players know they can change their armor */
}
