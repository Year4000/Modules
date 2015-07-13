/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.infractions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.year4000.utilities.configs.Config;
import net.year4000.utilities.configs.ConfigURL;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@ConfigURL(config = Settings.class, value = "https://api.year4000.net/configs/infractions")
public class Settings extends Config<Settings> {
    private static Settings inst;

    public static Settings get() {
        if (inst == null) {
            Settings config = new Settings();
            inst = config.getInstance(config);
        }

        return inst;
    }

    private String url;
    private String link;
}
