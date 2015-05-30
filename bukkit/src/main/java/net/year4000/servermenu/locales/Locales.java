package net.year4000.servermenu.locales;

import org.bukkit.command.CommandSender;

public enum Locales {
    MENU_SERVERS,
    MENU_PLAYERS,
    MENU_CLICK,
    MENU_OPEN,
    MENU_CLOSE,

    SERVER_ONLINE,
    SERVER_OFFLINE,
    SERVER_CLICK,
    SERVER_CONNECT,
    SERVER_GENERATE_TITLE,
    SERVER_GENERATE_DESCRIPTION,
    SERVER_GENERATE_NORMAL,
    SERVER_GENERATE_GENERATING,
    ;

    /** Translate the Locales enum to locale in the players language */
    public String translate(CommandSender player, String... args) {
        String code = this.name().toLowerCase().replaceAll("__", "-").replaceAll("_", ".");
        return new MessageFactory.Message(player).get(code, args);
    }

    /** Translate the Locales enum to locale in the players language */
    public String translate(String locale, String... args) {
        String code = this.name().toLowerCase().replaceAll("__", "-").replaceAll("_", ".");
        return new MessageFactory.Message(locale).get(code, args);
    }
}
