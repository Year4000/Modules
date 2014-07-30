package net.year4000.servermenu.menus;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.Data;
import lombok.Setter;
import net.year4000.servermenu.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

@Data
public final class APIManager {
    @Setter
    private static String api = Settings.get().getApi();
    private static final Gson gson = new Gson();

    private APIManager() {
        // util class
    }

    /** Get the data from the website */
    public static Reader getAPI() {
        try {
            InputStream url = new URI(api).toURL().openStream();
            return new InputStreamReader(url);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

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
        Map<String, ServerJson> servers = gson.fromJson(getAPI(), new TypeToken<Map<String, ServerJson>>(){}.getType());
        return servers.values();
    }
}
