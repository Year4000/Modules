package net.year4000.servermenu.menus;

import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;

@Log
public class APIManagerTest {
    @Before
    public void setUp() throws Exception {
        //APIManager.setApi("https://api.year4000.net/servers/");
    }

    @Test
    public void showServers() throws Exception {
        //APIManager.getServers().forEach(server -> log.info(server.toString()));
    }

    @Test
    public void showGroups() throws Exception {
        //APIManager.getGroups().forEach(server -> log.info(server.toString()));
    }
}