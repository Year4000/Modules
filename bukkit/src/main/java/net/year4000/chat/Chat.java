package net.year4000.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.year4000.chat.addons.Emoji;
import net.year4000.chat.addons.PlayerBadges;
import net.year4000.chat.formatter.FormatterManager;
import net.year4000.chat.message.Actor;
import net.year4000.chat.message.Message;
import net.year4000.chat.message.PlayerActor;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.bukkit.MessageUtil;

@ModuleInfo(
    name = "Chat",
    version = "1.6",
    description = "Chat formatting with features.",
    authors = {"Year4000"}
)
@ModuleListeners({
    // Built in
    ChatListener.class,
    // Addons
    PlayerBadges.class,
    Emoji.class
})
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
        // todo register commands to allow users to change their channels

        // Registered Formats lambdas are awesome
        registerDefaultFormats();
    }

    /** Register the default variables that are with the module */
    private void registerDefaultFormats() {
        FormatterManager.get().addFormatter("player", m -> m.getActor().getName());
        FormatterManager.get().addFormatter("locale", m -> {
            Actor actor = m.getActor();
            return actor instanceof PlayerActor ? ((PlayerActor) actor).getLocale() : "";
        });
        FormatterManager.get().addFormatter("server", Message::getServer);
        FormatterManager.get().addFormatter("message", m -> {
            Actor actor = m.getActor();
            boolean colors = actor instanceof PlayerActor && ((PlayerActor) actor).isUseColors();
            return colors ? MessageUtil.replaceColors(m.getMessage()) : m.getMessage();
        });
        FormatterManager.get().addFormatter("display", m -> {
            Actor actor = m.getActor();
            return actor instanceof PlayerActor ? ((PlayerActor) actor).getDisplay() : actor.getName();
        });
    }
}
