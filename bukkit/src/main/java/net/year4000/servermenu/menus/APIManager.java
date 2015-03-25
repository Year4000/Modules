package net.year4000.servermenu.menus;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.utilities.sdk.API;
import net.year4000.utilities.sdk.routes.players.PlayerCountJson;
import net.year4000.utilities.sdk.routes.servers.ServerJson;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class APIManager {
    private static final API api = new API();

    /** Get the list of groups */
    public static Collection<ServerJson.Group> getGroups() {
        Collection<ServerJson.Group> groups = new HashSet<>();

        // filter if group is not in collection then add it
        getServers().stream().forEach(server -> groups.add(server.getGroup()));

        return groups;
    }

    /** Get the list of groups */
    public static Collection<ServerJson.Group> getGroups(Collection<ServerJson> servers) {
        Collection<ServerJson.Group> groups = new HashSet<>();

        // filter if group is not in collection then add it
        servers.stream().forEach(server -> groups.add(server.getGroup()));

        return groups;
    }

    /** Get the collection of all the servers */
    public static Collection<ServerJson> getServers() {
        return api.getServers().getServersCollection();
    }

    /** Get the map of all players in a group */
    public static Map<String, PlayerCountJson.Count> getServerPlayerCount() {
        return api.getPlayerCount().getGroupsPlayerCount();
    }

    /** Get the count of all the players */
    public static PlayerCountJson.Count getNetworkPlayerCount() {
        return api.getPlayerCount().getNetworkPlayerCount();
    }
}
