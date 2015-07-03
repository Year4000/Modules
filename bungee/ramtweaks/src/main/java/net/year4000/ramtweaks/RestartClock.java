package net.year4000.ramtweaks;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.ramtweaks.messages.ShutdownMessage;

import java.util.concurrent.TimeUnit;

public class RestartClock implements Runnable {
    private ProxyServer proxy = ProxyServer.getInstance();
    @Getter
    private ScheduledTask task;

    public RestartClock() {
        task = proxy.getScheduler().schedule(DuckTape.get(), this, 10, 10, TimeUnit.MINUTES);
    }

    public void run() {
        // Check if last player online
        if (proxy.getOnlineCount() == 0) {
            new ShutdownMessage(10);
            task.cancel();
        }
    }
}
