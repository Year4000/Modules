package net.year4000.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.year4000.chat.formatter.Formatter;
import net.year4000.chat.formatter.FormatterManager;
import net.year4000.chat.message.Actor;
import net.year4000.chat.message.Message;
import net.year4000.chat.message.PlayerActor;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import org.bukkit.entity.Player;

@ModuleInfo(
    name = "Chat",
    version = "1.6",
    description = "Chat formatting with features.",
    authors = {"Year4000"}
)
@ModuleListeners({ChatListener.class})
public class Chat extends BukkitModule {
    public static final double CHAT_VERSION = 1.0;
    public static final Gson GSON = new GsonBuilder().setVersion(CHAT_VERSION).create();
    private static Chat inst;

    public static Chat get() {
        return  inst;
    }

    @Override
    public void load() {
        inst = this;
    }

    @Override
    public void enable() {
        // todo register commands

        // Registered Formats
        FormatterManager.get().addFormatter(new Formatter("player") {
            @Override
            public String value(Message message) {
                return message.getActor().getName();
            }
        });
        FormatterManager.get().addFormatter(new Formatter("message") {
            @Override
            public String value(Message message) {
                return message.getMessage();
            }
        });
        FormatterManager.get().addFormatter(new Formatter("display") {
            @Override
            public String value(Message message) {
                Actor actor = message.getActor();
                return actor instanceof PlayerActor ? ((PlayerActor) actor).getDisplay() : actor.getName();
            }
        });
    }
}
