package net.year4000.replacewords;

import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ModuleInfo(
    name = "ReplaceWords",
    version = "2.0",
    description = "Replace words",
    authors = {"Year4000", "Austin", "kacgal"}
)
@ModuleListeners({ReplaceWords.WordListener.class})
public class ReplaceWords extends BukkitModule {

    private static final String ANY_LETTER = "(?:\\\\W|\\.)*";
    private static final Pattern caps = Pattern.compile("^[A-Z]{2,}");
    private static final Random random = new Random();

    private static final HashMap<Pattern, String[]> replaces = new HashMap<Pattern, String[]>(){{
        put(toPattern("l|1|7,a|4|e|3,g,?y|i|n|g"), new String[]{
           "awesome", "fun", "incredible", "amazing", "cool", "wow"
        });
        put(toPattern("e|3|i|1,z,?i|1"), new String[]{
            "gg"
        });
        put(toPattern("f,u,c,k,?i|n|g,?e|r"), new String[]{
            "fantastic", "fantasy"
        });
        put(toPattern("m,o,t,h,e,r,?f,u,c,k,?i|n|g,?e|r"), new String[]{
            "fantastic", "fantasy"
        });
        put(toPattern("s,h,i,t,?y|i|n|g"), new String[]{
            "poop"
        });
        put(toPattern("b,i,t,c,h,?y|i|n|g"), new String[]{
            "dog"
        });
        put(toPattern("d,a,m,n"), new String[]{
            "darn"
        });
    }};

    private static Pattern toPattern(String s) {
        String pattern = "((?<= )(?i)" + ANY_LETTER;
        for (int i = 0; i < 2; i++) {
            for (String group : s.split(",")) {
                String t = "(?:";
                char num = '+';
                if (group.startsWith("?")) {
                    num = '*';
                    group = group.substring(1);
                }
                if (group.length() == 1) {
                    pattern += group + num + ANY_LETTER;
                    continue;
                }
                t += group;
                pattern += t + (num == '*' ? "|\\\\W|\\.)*" : ")+" + ANY_LETTER);
            }
            if (i == 0) {
                pattern += "|(?<!.)(?i)" + ANY_LETTER;
            }
        }
        return Pattern.compile(pattern + ")");
    }

    public static class WordListener implements Listener {
        @EventHandler
        public void onChat(AsyncPlayerChatEvent e) {
            for (Pattern pattern : replaces.keySet()) {
                Matcher matcher = pattern.matcher(e.getMessage());

                if (matcher.find()) {
                    try {
                        boolean allCaps = caps.matcher(matcher.group(1)).matches();
                        e.setMessage(matcher.replaceAll(getRandomPositiveWord(allCaps, pattern)));
                    } catch (IllegalArgumentException ex) {
                        log(ex, true);
                    }
                }
            }
        }
    }

    private static String getRandomPositiveWord(boolean caps, Pattern pattern) {
        String word = replaces.get(pattern)[(random.nextInt(replaces.get(pattern).length))];

        if (caps) {
            word = word.toUpperCase();
        }

        return word;
    }
}
