package net.year4000.serverlinker;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cubespace.Yamler.Config.Config;

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("unused")
public class Server extends Config {
    /** The name of the server that matches BungeeCord. */
    private String name;

    /** The name of the group */
    private String group;

    /** The host name of the server. */
    private String address;

    /** Is this a hub server */
    private Boolean hub;

    public boolean isHub() {
        return hub != null && hub;
    }
}
