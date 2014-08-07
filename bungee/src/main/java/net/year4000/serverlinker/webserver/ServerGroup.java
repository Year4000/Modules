package net.year4000.serverlinker.webserver;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.year4000.serverlinker.Server;
import net.year4000.serverlinker.Settings;
import net.year4000.utilities.config.Config;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ServerGroup extends Config {
    private String name;
    private String display;

    public ServerGroup(Server server) {
        name = server.getGroup();
        display = Settings.get().getGroups().get(name) == null ? name : Settings.get().getGroups().get(name);
    }
}
