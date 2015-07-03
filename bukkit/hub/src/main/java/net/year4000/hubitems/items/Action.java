/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.hubitems.items;

public enum Action {
    NONE,
    RIGHT,
    LEFT,
    ;

    public boolean isCorrectAction(org.bukkit.event.block.Action action) {
        if (this == Action.RIGHT) {
            return action == org.bukkit.event.block.Action.RIGHT_CLICK_AIR || action == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
        }

        return this == Action.LEFT && (action == org.bukkit.event.block.Action.LEFT_CLICK_AIR || action == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK);
    }
}
