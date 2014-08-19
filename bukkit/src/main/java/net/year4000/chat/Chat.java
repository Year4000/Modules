package net.year4000.chat;

import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;

@ModuleInfo(
    name = "Chat",
    version = "1.6",
    description = "Chat formatting with features.",
    authors = {"Year4000"}
)
@ModuleListeners({ChatListener.class})
public class Chat extends BukkitModule {
    public static final double CHAT_VERSION = 1.0;
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
        // todo register bungeelistener and commands
    }
}
