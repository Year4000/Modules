package net.year4000.tabwelcome;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.bungee.MessageUtil;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@ModuleInfo(
    name = "TabWelcome",
    version = "1.0",
    description = "Fight for 1.8 and show our brand in the tab list.",
    authors = {"Year4000"}
)
public class TabWelcome extends BungeeModule {
    private Set<String> shimmer = ImmutableSet.of("3", "b", "8", "7", "2", "a", "4", "c", "5", "d", "6", "e", "1", "9");
    private final String NAME = "Year4000";
    private final String IP = "mc.year4000.net";
    private Iterable<String> forever = Iterables.cycle(shimmer);
    private Iterator<String> color;

    @Override
    public void enable() {
        color = forever.iterator();

        ProxyServer.getInstance().getScheduler().schedule(DuckTape.get(), () -> {
            // change the message
            String b = "&" + color.next() + "&l";
            String name = b + "[&" + color.next() + "&l" + NAME + b + "]";
            String ip = "&b&l" + IP.replaceAll("\\.", "&3&l.&b&l");

            // send message
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                player.setTabHeader(MessageUtil.message(name + " &7| " + ip), null);
            }
        }, 1, TimeUnit.SECONDS);
    }
}
