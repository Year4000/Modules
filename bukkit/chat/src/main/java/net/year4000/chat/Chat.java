/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.chat;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.year4000.chat.addons.Emoji;
import net.year4000.chat.addons.PlayerBadges;
import net.year4000.chat.addons.PlayerNotice;
import net.year4000.chat.addons.Translator;
import net.year4000.chat.formatter.FormatterManager;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.entity.Player;

import java.util.Set;

@ModuleInfo(
    name = "Chat",
    version = "2.0",
    description = "Chat formatting with features.",
    authors = {"Year4000"}
)
@ModuleListeners({
    // Built in
    ChatListener.class,
    // Addons
    PlayerBadges.class,
    Emoji.class,
    PlayerNotice.class
})
public class Chat extends BukkitModule {
    public static final double CHAT_VERSION = 1.0;
    public static final String PLUGIN_CHANNEL = "BungeeCord";
    public static final String CHAT_FORMAT = "format";
    public static final String PLAYER_DISPLAY = "display";
    public static final String PLAYER_NAME = "player";
    public static final String PLAYER_LOCALE = "player";
    public static final String PLAYER_COLORS = "colors";
    private static Set<String> VIPS = ImmutableSet.of("theta", "mu", "pi", "sigma", "phi", "delta", "omega");
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
        // todo register commands to allow users to manage their channels

        // Registered Formats lambdas are awesome
        registerDefaultFormats();
    }

    /** Register the default variables that are with the module */
    private void registerDefaultFormats() {
        FormatterManager.get().addFormatter(Chat.PLAYER_NAME, Message::getActorName);
        FormatterManager.get().addFormatter("server", Message::getServer);
        FormatterManager.get().addFormatter("message", m -> Boolean.parseBoolean(m.getMeta(Chat.PLAYER_COLORS)) ? MessageUtil.replaceColors(m.getMessage()) : m.getMessage());
        FormatterManager.get().addFormatter(Chat.PLAYER_DISPLAY, m -> m.isMeta(Chat.PLAYER_DISPLAY) ? m.getMeta(Chat.PLAYER_DISPLAY) : m.getActorName());
        FormatterManager.get().addFormatter(Chat.PLAYER_LOCALE, m -> m.isMeta(Chat.PLAYER_LOCALE) ? m.getMeta(Chat.PLAYER_LOCALE) : m.getActorName());
    }

    /** Is the selected player a VIP */
    public static boolean isVIP(Player player) {
        for (String permission : VIPS) {
            if (player.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }
}
