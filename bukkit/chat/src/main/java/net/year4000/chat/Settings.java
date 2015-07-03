/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.utilities.config.Comment;
import net.year4000.utilities.config.Config;
import net.year4000.utilities.config.InvalidConfigurationException;

import java.io.File;

@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends Config {
    private static Settings inst;

    public Settings() {
        try {
            CONFIG_HEADER = new String[] {"Chat Configuration"};
            CONFIG_FILE = new File(Chat.get().getDataFolder(), "config.yml");
            init();
        } catch (InvalidConfigurationException e) {
            Chat.log(e, true);
        }
    }

    /** Return its self if has not been created before */
    public static Settings get() {
        if (inst == null) {
            inst = new Settings();
        }

        return inst;
    }

    @Comment("The format for the chat on the same server.")
    private String chatFormat = "{player}: {message}";

    @Comment("The format of the chat when you see it on another server.")
    private String serverFormat = "[{server}] {player}: {message}";
}
