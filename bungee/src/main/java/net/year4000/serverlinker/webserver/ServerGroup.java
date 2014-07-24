package net.year4000.serverlinker.webserver;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.cubespace.Yamler.Config.Config;
import net.year4000.serverlinker.Server;
import net.year4000.serverlinker.Settings;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ServerGroup extends Config {
    private String name;
    private String display;

    public ServerGroup(Server server) {
        name = server.getGroup();
        display = Settings.get().getGroups().get(name);
    }
}
