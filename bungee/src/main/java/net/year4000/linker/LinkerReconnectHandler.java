package net.year4000.linker;

import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LinkerReconnectHandler implements ReconnectHandler {
    @Override
    public ServerInfo getServer(ProxiedPlayer player) {
        return Linker.instance.getHub();
    }

    @Override
    public void setServer(ProxiedPlayer player) {
        // Not needed
    }

    @Override
    public void save() {
        // Not needed
    }

    @Override
    public void close() {
        // Not needed
    }
}
