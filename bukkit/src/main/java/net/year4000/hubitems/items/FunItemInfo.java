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

    /** The cost of the item per use */
    public float mana();
}
