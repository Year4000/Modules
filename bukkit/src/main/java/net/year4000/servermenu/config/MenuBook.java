package net.year4000.servermenu.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.utilities.config.Config;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class MenuBook extends Config {
    /** The title of the item to open */
    private String title;

    /** Should the servers in thei menu show player count */
    private boolean players;

    /** Should the servers in this menu show motd */
    private boolean motd;

    /** The list of groups that this menu manages */
    private List<String> groups;
}
