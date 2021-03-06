/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.dashboard;

import com.google.common.base.Ascii;
import com.google.common.base.Splitter;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.regex.Pattern;

public final class SidebarManager {
    private int blankCounter = 1;
    private Set<String> staticScores = new LinkedHashSet<>();
    private List<String> customTeams = new ArrayList<>();
    private Set<Object[]> dynamicScores = new LinkedHashSet<>();

    /** Add a blank line */
    public SidebarManager addBlank() {
        String line = "";

        for (int i = 0; i < blankCounter; i++) {
            line += " ";
        }

        blankCounter++;
        staticScores.add(line);

        return this;
    }

    /** Add a line */
    public SidebarManager addLine(String line) {
        // When scores are the same append a blank until the string is different.
        while (staticScores.contains(line)) {
            line += "&r";
        }

        staticScores.add(line);
        return this;
    }

    /** Add a line */
    public SidebarManager addLine(String line, int number) {
        dynamicScores.add(new Object[]{line, number});
        return this;
    }

    /** Creates a team to allow for 48 chars and return the display name */
    private String buildTeam(Scoreboard scoreboard, String name) {
        name = MessageUtil.replaceColors(Ascii.truncate(name, 48, ""));

        if (name.length() <= 16) {
            return name;
        }

        Team team = scoreboard.registerNewTeam(Ascii.truncate("txt:" + scoreboard.getTeams().size(), 16, ""));
        Iterator<String> part = Splitter.fixedLength(16).split(name).iterator();
        String prefix = part.next(), result = part.next(), nameHack, lastColor = "", lastFormat = "";
        int size = prefix.length();

        // Find and set last used color and color format
        for (int i = 0; i < size - 2; i++) {
            String key = prefix.substring(i, i + 2);

            if (ChatColor.COLOR_CHAR != key.charAt(0)) continue;

            // Match last color
            if (Pattern.matches("[0-9a-fA-F]", String.valueOf(key.charAt(1)))) {
                lastColor = key;
            }

            // Match last format
            if (Pattern.matches("[K-Ok-o]", String.valueOf(key.charAt(1)))) {
                lastFormat = key;
            }
        }

        while (customTeams.contains(result)) {
            result = lastColor + lastFormat + result;
        }

        customTeams.add(result);
        nameHack = result;

        // Append the suffix to the name hack
        if (part.hasNext()) {
            nameHack += part.next();
        }

        // Split the nameHack into the name and suffix
        nameHack = MessageUtil.replaceColors(Ascii.truncate(nameHack, 32, ""));
        Iterator<String> suffix = Splitter.fixedLength(16).split(nameHack).iterator();
        result = suffix.next();

        if (suffix.hasNext()) {
            team.setSuffix(suffix.next());
        }

        team.setPrefix(prefix);
        team.addEntry(result);
        return result;
    }

    public Objective buildSidebar(Scoreboard scoreboard, String title) {
        String id = String.valueOf(title.hashCode());
        Objective objective;

        // If exists reset scores
        if (scoreboard.getObjective(id) != null) {
            // todo re-enable this fast switch when full convert to 1.8
            /*objective = scoreboard.getObjective(id);

            objective.setDisplayName(Common.truncate(MessageUtil.replaceColors(title), 32));
            scoreboard.getEntries().stream()
                .filter(name -> !staticScores.contains(name))
                .forEach(scoreboard::resetScores);*/

            // TODO This is a 1.7 client HACK fix... remove when convert to 1.8
            scoreboard.getObjective(id).unregister();
            objective = scoreboard.registerNewObjective(id, "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(Ascii.truncate(MessageUtil.replaceColors(title), 32, ""));

            buildScores(scoreboard, objective);
        }
        else {
            // Register it
            objective = scoreboard.registerNewObjective(id, "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(Ascii.truncate(MessageUtil.replaceColors(title), 32, ""));

            buildScores(scoreboard, objective);
        }

        return objective;
    }

    public void buildScores(Scoreboard scoreboard, Objective objective) {
        Iterator<String> scores = staticScores.iterator();

        // Add static scores that are the order they are in the list
        for (int i = 0; i < staticScores.size(); i++) {
            Score score = objective.getScore(buildTeam(scoreboard, scores.next()));
            score.setScore(-(i + 1));
        }

        // Add dynamic scores that don't depend on statics
        for (Object[] lines : dynamicScores) {
            // todo re-enable this fast switch when full convert to 1.8
            //if (scoreboard.getEntries().contains(lines[0])) continue;

            String scoreId = buildTeam(scoreboard, ((String) lines[0]));
            int scoreInput = (Integer) lines[1];

            // Set default score to fix Bukkit / Minecraft cant start with 0
            if (scoreInput == 0) {
                objective.getScore(scoreId).setScore(1);
                objective.getScore(scoreId).setScore(0);
            }
            // Handle score normally
            else {
                objective.getScore(scoreId).setScore(scoreInput);
            }
        }
    }
}
