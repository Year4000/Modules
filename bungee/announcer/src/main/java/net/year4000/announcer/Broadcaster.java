package net.year4000.announcer;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.announcer.messages.Message;
import net.year4000.utilities.bungee.MessageUtil;

import java.util.List;
import java.util.Random;

public class Broadcaster implements Runnable {
    private static final Random rand = new Random(System.currentTimeMillis());
    private static Settings settings = Settings.get();
    private List<String> messages;
    private String server;
    private int index = 0;

    public Broadcaster(String server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            messages = settings.getMessages(server);

            // Get the messages and the index.
            if (settings.isRandom()) {
                index = Math.abs(rand.nextInt() % messages.size());
            }

            // Set the position to the messages.
            if (index == messages.size()) {
                index = 0;
            }

            // Broadcast the message
            for (ProxiedPlayer player : ProxyServer.getInstance().getServerInfo(server).getPlayers()) {
                // Don't run any messages is the message is blank.
                if (messages.get(index).isEmpty()) break;

                // Replace and translate the message.
                String message = messages.get(index);

                // Broadcast the message if the player has the permission.
                if (player.hasPermission("announcer.receiver")) {
                    player.sendMessage(parseBroadcast(player, message));
                }
            }
        }
        catch (NullPointerException e) {
            Announcer.debug("No messages found for " + server);
        }
        catch (Exception e) {
            Announcer.debug(e.getMessage());
        }

        Announcer.debug("Running a " + messages.get(index) + " for: " + server);
        ++index;
    }

    /**
     * Parse a message to be used.
     * @param message The message.
     * @return The parsed message.
     */
    public static BaseComponent[] parseBroadcast(ProxiedPlayer player, String message) throws Exception {
        // Replace message if found a translation key
        message = new Message(player).get(message);
        if (player != null) {
            message = message.replaceAll("\\{player\\}", player.getName());
            message = message.replaceAll("\\{server\\}", player.getServer().getInfo().getName());
        }

        try {
            // Raw Message
            if (MessageUtil.isRawMessage(message)) {
                return MessageUtil.merge(settings.getPrefix(), MessageUtil.parseMessage(message));
            }
            // Simple Classic Message
            else {
                return MessageUtil.message(settings.getPrefix() + message);
            }
        } catch (NullPointerException e) {
            Announcer.debug(e.getMessage());
            throw new Exception("Message could not find the prefix.");
        } catch (Exception e) {
            Announcer.debug(e.getMessage());
            throw new Exception("Message could not be parsed.");
        }
    }
}
