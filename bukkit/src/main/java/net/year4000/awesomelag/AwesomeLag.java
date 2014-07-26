package net.year4000.awesomelag;

import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.core.module.ModuleInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ModuleInfo(
    name = "AwesomeLag",
    version = "1.2",
    description = "Get rid of lag",
    authors = {"Year4000", "Austin"}
)
@ModuleListeners({AwesomeLag.LagListener.class})
public class AwesomeLag extends BukkitModule {
    private static final Pattern lag = Pattern.compile("((?<= )(?i)(?:\\W|_)*(?:l|1|7)+(?:\\W|_)*(?:a|4)+(?:\\W|_)*g+(?:\\W|_)*(?:y|i|n|g|\\W)*|(?<!.)(?i)(?:\\W|_)*l+(?:\\W|_)*(?:a|4|)+(?:\\W|_)*g+(?:\\W|_)*(?:y|i|n|g|\\W)*)");
    private static final Pattern caps = Pattern.compile("^[A-Z]{2,}");
    private static final Random random = new Random();

    private static List<String> words = new ArrayList<String>(){{
        add("awesome");
        add("fun");
        add("incredible");
        add("amazing");
        add("cool");
        add("wow");
    }};

    public static class LagListener implements Listener {
        @EventHandler
        public void onChat(AsyncPlayerChatEvent e) {
            Matcher matcher = lag.matcher(e.getMessage());

            if(matcher.find()) {
                boolean allCaps = caps.matcher(matcher.group(1)).matches();
                e.setMessage(matcher.replaceAll(getRandomPositiveWord(allCaps)));
            }
        }
    }

    private static String getRandomPositiveWord(boolean caps) {
        String word = words.get(random.nextInt(words.size()));

        if(caps) {
            word = word.toUpperCase();
        }

        return word;
    }
}
