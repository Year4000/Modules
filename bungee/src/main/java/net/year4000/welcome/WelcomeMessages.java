package net.year4000.welcome;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.utilities.bungee.BungeeLocale;
import net.year4000.utilities.bungee.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WelcomeMessages extends BungeeLocale {
    private ProxiedPlayer player;

    public WelcomeMessages(ProxiedPlayer player) {
        super(player);
        this.player = player;
        localeManager = WelcomeMessagesManager.get();
    }

    public List<BaseComponent[]> getMotd(String code) {
        Properties properties = localeManager.getLocale(localeManager.isLocale(code) ? code : DEFAULT_LOCALE);
        int loopSize = properties.stringPropertyNames().size();
        List<BaseComponent[]> lines = new ArrayList<>();

        for (int i = 0 ; i < loopSize; i ++) {
            String message = (String) properties.getOrDefault("motd." + i, null);

            if (message != null) {
                // Replace vars
                message = message.replaceAll("\\{player\\}", player.getName());
                message = message.replaceAll("\\{display\\}", player.getDisplayName());
                message = message.replaceAll("\\{locale\\}", player.getLocale().toString());

                // Add message to list
                // Raw Message
                if (MessageUtil.isRawMessage(message)) {
                    try {
                        lines.add(MessageUtil.parseMessage(message));
                    } catch (Exception e) {
                        Welcome.debug(e, true);
                    }
                }
                // Simple Classic Message
                else {
                    lines.add(MessageUtil.message(message));
                }
            }
        }

        return lines;
    }
}
