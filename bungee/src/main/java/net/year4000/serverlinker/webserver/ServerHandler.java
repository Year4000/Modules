package net.year4000.serverlinker.webserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.serverlinker.ServerLinker;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServerHandler extends AbstractHandler {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static WebThread server;

    @Override
    public void handle(String s, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
        httpResponse.setContentType("text/json;charset=utf-8");
        httpResponse.addHeader("Refresh", "30"); // let browsers update
        httpResponse.addHeader("Access-Control-Allow-Origin", "*"); // Acess from *
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
        gson.toJson(StatusCollection.get().getNonHiddenServers(), httpResponse.getWriter());
    }

    public static void startWebServer() {
        server = new WebThread();
    }

    public static void stopWebServer() {
        try {
            server.webServer.stop();
            server.task.cancel();
        } catch (Exception e) {
            ServerLinker.debug(e, false);
        }
    }

    public static class WebThread implements Runnable {
        ScheduledTask task;
        Server webServer;

        public WebThread() {
            task = ProxyServer.getInstance().getScheduler().runAsync(DuckTape.get(), this);
        }

        @Override
        public void run() {
            try {
                webServer = new Server(5555);
                webServer.setHandler(new ServerHandler());
                webServer.start();
                webServer.join();
            } catch (Exception e) {
                ServerLinker.debug(e, false);
            }
        }
    }
}
