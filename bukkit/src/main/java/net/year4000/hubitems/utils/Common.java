package net.year4000.hubitems.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.NBTTagList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Common {
    public static double manaConverter(float mana) {
        return ((double) mana * .10) * 1000;
    }


    public static String[] loreDescription(String string) {
        return splitIntoLine(string, 30);
    }

    public static String[] splitIntoLine(String input, int maxCharInLine){

        StringTokenizer tok = new StringTokenizer(input, " ");
        StringBuilder output = new StringBuilder(input.length());
        int lineLen = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            while(word.length() > maxCharInLine){
                output.append(word.substring(0, maxCharInLine-lineLen) + "\n");
                word = word.substring(maxCharInLine-lineLen);
                lineLen = 0;
            }

            if (lineLen + word.length() > maxCharInLine) {
                output.append("\n");
                lineLen = 0;
            }
            output.append(word + " ");

            lineLen += word.length() + 1;
        }
        // output.split();
        // return output.toString();
        return output.toString().split("\n");
    }

    public static Location getLooking(Player player) {
        Block block = player.getTargetBlock(null, 150);

        while (block.getType() != Material.AIR) {
            block = block.getRelative(BlockFace.UP);
        }

        return block.getLocation();
    }

    public static List<Location> getBox(Location center, double boxSize) {
        ArrayList<Location> boxPoints = new ArrayList<>();
        boxPoints.add(center.clone().add(-boxSize, 0, -boxSize));
        boxPoints.add(center.clone().add(boxSize, 0, -boxSize));
        boxPoints.add(center.clone().add(boxSize, 0, boxSize));
        boxPoints.add(center.clone().add(-boxSize, 0, boxSize));
        return boxPoints;
    }

    public static List<Location> getLines(Location startingPoint, Location endingPoint, double distanceBetweenParticles) {
        return getLines(startingPoint, endingPoint,
            (int) Math.ceil(startingPoint.distance(endingPoint) / distanceBetweenParticles));
    }

    public static List<Location> getLines(Location startingPoint, Location endingPoint, int amountOfPoints) {
        startingPoint = startingPoint.clone();
        Vector vector = endingPoint.toVector().subtract(startingPoint.toVector());
        vector.normalize();
        vector.multiply(startingPoint.distance(endingPoint) / (double) amountOfPoints);
        ArrayList<Location> locs = new ArrayList<>();
        for (int i = 0; i < amountOfPoints; i++) {
            locs.add(startingPoint.add(vector).clone());
        }
        return locs;
    }

    public static ArrayList<Location> getPointsCircle(Location center, int pointsAmount, double distance) {
        ArrayList<Location> locs = new ArrayList<Location>();
        for (int i = 0; i < pointsAmount; i++) {
            double angle = ((2 * Math.PI) / (double) pointsAmount) * (double) i;
            double x = distance * Math.cos(angle);
            double z = distance * Math.sin(angle);
            Location loc = center.clone().add(x, 0, z);
            locs.add(loc);
        }
        return locs;
    }

    public static ItemStack addGlow(ItemStack item) {
        net.minecraft.server.v1_7_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
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
}
