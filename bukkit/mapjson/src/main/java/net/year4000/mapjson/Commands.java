/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapjson;

import com.google.common.collect.ImmutableMap;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.returns.TripleReturn;
import org.bukkit.Location;

import java.util.Set;
import java.util.function.Consumer;

public class Commands {
    /** MapNodes currently uses the Block not location cord */
    private static int loc(int loc) {
        return loc < 0 ? loc - 1 : loc;
    }

    /** Color the string to make json look pretty */
    private static String colorJson(String json) {
        String colors = json
            .replaceAll("[\"']", "&8'&6")
            .replaceAll("\\{", "&7{&e")
            .replaceAll("}", "&7}&e")
            .replaceAll("\\[", "&7[&e")
            .replaceAll("]", "&7]&e")
            .replaceAll(",", "&f,&6")
            .replaceAll(":", "&f:&6")
            ;
        return MessageUtil.replaceColors(colors);
    }

    /** The point command */
    static Consumer<TripleReturn<MapJson, UserSession, String[]>> point = (cmd) -> {
        UserSession player = cmd.getB();
        boolean rotation = cmd.getC().length > 1 && cmd.getC()[1].equalsIgnoreCase("-rotation");

        Location location = player.player().getLocation();
        LocationVector vector;

        // Add yaw and pitch if rotation flag is set
        if (rotation) {
            vector = new LocationVector(loc(location.getBlockX()), location.getBlockY(), loc(location.getBlockZ()), (int) location.getYaw(), (int) location.getPitch());
        }
        else {
            vector = new LocationVector(loc(location.getBlockX()), location.getBlockY(), loc(location.getBlockZ()));
        }

        player.addSpawn(vector);
        String cord = vector.getX() + "&7, &e" + vector.getY() + "&7, &e" + vector.getZ();

        if (rotation) {
            cord += " &7(&eyaw&7: &e" + vector.getYaw() + "&7, &epitch&7: &e" + vector.getPitch() + "&7)";
        }

        player.sendMessage("&6Spawn point added&7: &e" + cord);
    };

    /** Save the points and wipe spawn locations */
    static Consumer<TripleReturn<MapJson, UserSession, String[]>> savePoints = (cmd) -> {
        MapJson mapJson = cmd.getA();
        UserSession player = cmd.getB();
        boolean screen = cmd.getC().length > 1 && cmd.getC()[1].equals("-");

        Set<LocationVector> spawns = player.getSpawns();

        // Output to screen
        if (screen) {
            player.sendMessage("&6Spawns json output&7.");

            int count = 0;
            player.sendMessage(colorJson("'spawns': {"));

            for (LocationVector vector : spawns) {
                count++;
                String comma = count != spawns.size() ? "," : "";
                player.sendMessage(" " + colorJson(vector.toString() + comma));
            }

            player.sendMessage(colorJson("}"));

        }
        // Save to file
        else {
            mapJson.writeLocations(player, spawns.toArray(new LocationVector[spawns.size()]));
            spawns.clear();

            player.sendMessage("&6Spawns have been saved check data folder for snippet&7.");
        }
    };

    // Command map must be bellow commands
    static final ImmutableMap<String, Consumer<TripleReturn<MapJson, UserSession, String[]>>> commands = ImmutableMap.<String, Consumer<TripleReturn<MapJson, UserSession, String[]>>>builder()
        .put("point", point)
        .put("savepoints", savePoints)
        .build();
}
