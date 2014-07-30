package net.year4000.mathinchat;

import com.google.common.collect.ImmutableSet;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.core.module.ModuleInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Set;

@ModuleInfo(
    name = "MathInChat",
    version = "1.0",
    description = "Calculate math expressions in chat using JavaScript",
    authors = {"Year4000"}
)
@ModuleListeners({MathInChat.MathListener.class})
public class MathInChat extends BukkitModule {
    static Set<Character> expressions = ImmutableSet.of('+', '-', '*', '^', '%', '\\');
    static ScriptEngineManager mgr = new ScriptEngineManager();
    static ScriptEngine engine = mgr.getEngineByName("JavaScript");

    public static class MathListener implements Listener {
        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
        public void onChat(AsyncPlayerChatEvent event) {
            String message = event.getMessage();

            if (isExpression(message)) {
                event.setCancelled(true);

                try {
                    event.getPlayer().sendMessage(message + "=" + engine.eval(message));
                } catch (ScriptException e) {
                    event.getPlayer().sendMessage(e.getMessage());
                }
            }
        }

        private boolean isExpression(String message) {
            for (Character ex : expressions) {
                if (message.contains(ex.toString())) {
                    return true;
                }
            }

            return false;
        }
    }
}
