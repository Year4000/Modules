/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.announcer.messages;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.utilities.bungee.BungeeLocale;

public class Message extends BungeeLocale {
    public Message(ProxiedPlayer player) {
        super(player);
        this.localeManager = MessageManager.get();
    }
}
