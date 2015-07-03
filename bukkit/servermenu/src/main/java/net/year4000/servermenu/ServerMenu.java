/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.servermenu;

import lombok.Getter;
import net.year4000.ducktape.bukkit.module.BukkitModule;
import net.year4000.ducktape.bukkit.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.servermenu.gui.AbstractGUI;
import net.year4000.servermenu.gui.MainMenuGUI;
import net.year4000.servermenu.gui.MapNodesGUI;
import net.year4000.servermenu.locales.MessageFactory;
import net.year4000.utilities.MessageUtil;
import net.year4000.utilities.sdk.API;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@ModuleInfo(
    name = "ServerMenu",
    version = "3.0",
    description = "The menu that lets you connect to the servers",
    authors = {"Year4000"}
)
@ModuleListeners({Listeners.class})
public class ServerMenu extends BukkitModule {
    public static final String MASTER_KEY = System.getenv("Y4K_KEY");
    public static API api = new API(MASTER_KEY);

    /** Make sure instance has loaded before one calls it */
    public static ServerMenu inst;
    /** The menu GUIs */
    @Getter
    private List<AbstractGUI> menus = new ArrayList<>();
    private TaskProcessor<AbstractGUI> processing;

    @Override
    public void enable() {
        inst = this;
        Settings settings = Settings.get();

        // Add the main gui
        log("Loading: Main Menu GUI");
        menus.add(new MainMenuGUI("us", settings.getMenus()));
        // Add the sub menus
        for (Settings.Menu menu : settings.getMenus()) {
            log("Loading: " + menu.getName());
            if (menu.isMapNodes()) {
                menus.add(new MapNodesGUI(menu));
            }
            else {
                // todo menus.add(new SubMenuGUI())
            }
        }

        // Set up the processor
        processing = new TaskProcessor<>(menus);
        processing.setWait(() -> Bukkit.getOnlinePlayers().size() == 0);
        processing.process();
    }

    @Override
    public void disable() {
        processing.end();
    }

    /** Get the collection of locales */
    public Collection<Locale> getLocales() {
        return MessageFactory.get().getLocales().keySet();
    }

    /** Format the region code */
    public static String formatRegion(String region) {
        return MessageUtil.replaceColors("&8[" + region.toUpperCase() + "]");
    }
}
