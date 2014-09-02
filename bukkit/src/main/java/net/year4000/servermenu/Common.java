package net.year4000.servermenu;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Common {
    public static ItemStack addGlow(ItemStack item) {
        net.minecraft.server.v1_7_R4.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    public static ItemStack removeGlow(ItemStack item) {
        net.minecraft.server.v1_7_R4.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) return item;
        tag = nmsStack.getTag();
        tag.set("ench", null);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
}
