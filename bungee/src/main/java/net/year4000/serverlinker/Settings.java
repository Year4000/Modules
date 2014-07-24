package net.year4000.serverlinker;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Config;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class Settings extends Config {
    private static Settings inst;

    public Settings() {
        try {
            CONFIG_HEADER = new String[] {"ServerLinker"};
            CONFIG_FILE = new File(ServerLinker.getInstance().getDataFolder(), "config.yml");
            init();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Settings get() {
        if (inst == null) {
            inst = new Settings();
        }

        return inst;
    }

    @Comment("The url to grab the locales from")
    private String url = "https://git.year4000.net/year4000/locales/raw/master/net/year4000/serverlinker/locales/";

    @Comment("The name of the network, used for messages.")
    private String network = "&3[&bYear4000 Network&3]";

    @Comment("The list of the groups key=id value=display")
    private Map<String, String> groups = new HashMap<>();

    @Comment("The list of servers that this network runs.")
    private List<Server> servers = new ArrayList<Server>() {{
        add(new Server(){{
            setName("Year4000 Network");
            setGroup("network");
            setAddress("mc.year4000.net:25565");
        }});
    }};

    /** Get the server by its name */
    public Server getServer(String name) {
        return servers.stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst().get();
    }
}