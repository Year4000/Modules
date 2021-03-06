/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.servermenu.views;

import net.year4000.servermenu.InventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public interface IconView {
    /** Make the item that will be used for the menu view */
    ItemStack make();

    /** Process what happens when you click on the icon */
    void action(Locale locale, Player player, InventoryGUI gui);
}
