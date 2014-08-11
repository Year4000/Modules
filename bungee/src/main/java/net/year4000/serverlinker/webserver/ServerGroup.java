package net.year4000.serverlinker.webserver;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.serverlinker.Server;
import net.year4000.serverlinker.Settings;

@Data
@NoArgsConstructor
public class ServerGroup {
    private String name;
    private String display;

    public ServerGroup(Server server) {
        name = server.getGroup();
        display = Settings.get().getGroups().get(name) == null ? name : Settings.get().getGroups().get(name);
    }

    /** Is this group hidden */
    public boolean isHidden() {
        return name.startsWith(".");
    }
}
