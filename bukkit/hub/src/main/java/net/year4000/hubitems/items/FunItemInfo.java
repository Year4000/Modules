/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.hubitems.items;

import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunItemInfo {
    /** The name of the item */
    public String name();

    /** The icon to use */
    public Material icon();

    /** What this item does use locale codes */
    public String description();

    /** The permission required to use this item */
    public String[] permission() default {};

    /** The permission locale message */
    public String permissionLocale() default "";

    /** Is this ability passive */
    public PassiveState passive() default PassiveState.NONE;

    /** The cost of the item per use */
    public float mana() default 0F;

    /** true for right click false for left click */
    public Action action() default Action.NONE;
}
