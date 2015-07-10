/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapjson;

import com.google.common.collect.Lists;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.FileUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.returns.TripleReturn;
import org.apache.commons.io.IOUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@ModuleInfo(
    name = "MapJson",
    version = "1.0.0",
    description = "Tools to aid creation of MapNodes json",
    authors = {"Year4000"}
)
@ModuleListeners({MapJson.EventHandlers.class})
public class MapJson extends BukkitModule {
    private static final String SNIPPET = "snipet-%s.json";
    private static MapJson mapJson;
    private List<UserSession> sessions = Lists.newArrayList();
    private WeakHashMap<Player, UserSession> sessionCache = new WeakHashMap<>();

    @Override
    public void enable() {
        mapJson = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
    }

    /** Get the user session of the player if there is one */
    public UserSession getSession(Player player) {
        if (!sessionCache.isEmpty() && sessionCache.containsKey(player)) {
            return sessionCache.get(player);
        }

        for (UserSession session : sessions) {
            if (player.equals(session.player())) {
                sessionCache.put(player, session);
                return session;
            }
        }

        throw new IllegalArgumentException("No user session");
    }

    /** Write the string array to the file */
    private void write(String name, String... lines) {
        checkNotNull(name, "name");
        checkArgument(lines.length > 0, "lines.length");

        String fileName = String.format(SNIPPET, name.toLowerCase().replaceAll(" ", "_"));
        File snippet = new File(getDataFolder(), fileName);

        try {
            if (!snippet.exists()) {
                snippet.createNewFile();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (FileWriter writer = new FileWriter(snippet)) {
            for (String line : lines) {
                String tmp = "";

                // Replace singles with doubles and doubles with singles
                for (char c : line.toCharArray()) {
                    if (c == '"') {
                        tmp += "'";
                    }
                    else if (c == '\'') {
                        tmp += "\"";
                    }
                    else {
                        tmp += c;
                    }
                }

                writer.write(tmp + "\n");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Will write the locations to the snippet file */
    public void writeLocations(UserSession session, LocationVector... locations) {
        checkNotNull(session, "session");
        checkArgument(locations.length > 0, "locations.length");
        List<String> lines = Lists.newArrayList();
        int count = 0;

        lines.add("'spawns': [");

        for (LocationVector vector : locations) {
            count++;
            String comma = count != locations.length ? "," : "";
            lines.add("    " + vector.toString() + comma);
        }

        lines.add("]");

        write("locations", lines.toArray(new String[lines.size()]));
    }

    public static class EventHandlers implements Listener {

        @EventHandler
        public void login(PlayerJoinEvent event) {
            debug("Login: " + event.toString());
            mapJson.sessions.add(new UserSession(event.getPlayer()));
        }

        @EventHandler
        public void command(PlayerCommandPreprocessEvent event) {
            debug("Command: " + event.toString());

            try {
                String raw = event.getMessage() + " ";
                String message = raw.substring(1, raw.indexOf(" ")); // Grab first command
                debug("Message: " + message);

                Consumer<TripleReturn<MapJson, UserSession, String[]>> cmd = Commands.commands.get(message);

                if (cmd != null) {
                    UserSession session = mapJson.getSession(event.getPlayer());
                    TripleReturn<MapJson, UserSession, String[]> data = new TripleReturn<>(mapJson, session, raw.split(" "));
                    cmd.accept(data);
                    event.setCancelled(true);
                }
            }
            catch (Exception e) {
                event.getPlayer().sendMessage(MessageUtil.replaceColors(" &7[&e!&7] &4" + e.getMessage()));
                event.setCancelled(true);
            }
        }
    }
}
