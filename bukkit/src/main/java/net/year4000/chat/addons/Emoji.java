package net.year4000.chat.addons;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import net.year4000.chat.Chat;
import net.year4000.chat.events.MessageReceiveEvent;
import net.year4000.chat.message.BaseMessage;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.commands.BukkitWrappedCommandSender;
import net.year4000.utilities.bukkit.commands.Command;
import net.year4000.utilities.bukkit.commands.CommandContext;
import net.year4000.utilities.bukkit.commands.CommandException;
import net.year4000.utilities.bukkit.pagination.SimplePaginatedResult;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.regex.Pattern;

public class Emoji implements Listener {
    private static final Map<String[], Character> EMOJI = ImmutableMap.<String[], Character>builder()
        // Faces
        .put(keys(":)", "(:"), '\u263A')
        .put(keys(":(", "):"), '\u2639')
        .put(keys(":P", ":p", "8)", ":D", ":d",";)", "(;", ":o", ":O", ":0", ";(", ");", ":|"), '\u263B')

        // Chest
        .put(keys(":kink:"), '\u2654')
        .put(keys(":queen:"), '\u2655')
        .put(keys(":rook:"), '\u2656')
        .put(keys(":bishop:"), '\u2657')
        .put(keys(":knight:"), '\u2658')
        .put(keys(":pawn:"), '\u2659')

        // Cards
        .put(keys(":heart:", "<3", "E>"), '\u2764')
        .put(keys(":spade:"), '\u2660')
        .put(keys(":clover:", ":luck:"), '\u2663')
        .put(keys(":diamond:"), '\u2666')

        // Music
        .put(keys(":quarternote:"), '\u2669')
        .put(keys(":note:", ":eightnote:"), '\u266A')
        .put(keys(":beamedeightnote:"), '\u266B')
        .put(keys(":beamedsiztenthnote:"), '\u266C')
        .put(keys(":flatsign:"), '\u266D')
        .put(keys(":naturalsign:"), '\u266E')
        .put(keys(":sharpsign:"), '\u266F')

        // Misc
        .put(keys(":yin:", ":yang:"), '\u262F')
        .put(keys(":peace:"), '\u262E')
        .put(keys(":hourglass:"), '\u23F3')
        .put(keys(":skull:"), '\u2620')
        .put(keys(":biohazard:"), '\u2623')
        .put(keys(":nuke:"), '\u2622')
        .put(keys(":star:"), '\u2605')
        .put(keys(":sun:"), '\u2600')
        .put(keys(":copyright:"), '\u00A9')
        .put(keys(":registered:"), '\u00AE')
        .build();

    /** Short hand to make key in the emoji */
    private static String[] keys(String... keys) {
        return keys;
    }

    public Emoji() {
        Chat.get().registerCommand(Emoji.class);
    }

    @EventHandler
    public void onReceive(MessageReceiveEvent event) {
        BaseMessage message = (BaseMessage) event.getMessage();

        message.setMessage(replaceEmoji(message.getMessage()));
    }

    /** Replace all the Emoji indexes from the message */
    private String replaceEmoji(String message) {
        String replace = message;

        for (Map.Entry<String[], Character> entry : EMOJI.entrySet()) {
            for (String key : entry.getKey()) {
                replace = replace.replaceAll(Pattern.quote(key), String.valueOf(entry.getValue()));
            }
        }

        return replace;
    }

    @Command(
        aliases = {"emoji", "emoticons"},
        desc = "Show all the emoticons that we currently support",
        max = 1
    )
    public static void loadedChunks(CommandContext args, CommandSender sender) throws CommandException {
        final int MAX_PER_PAGE = 8;
        new SimplePaginatedResult<Map.Entry<String[], Character>>("Emoticons", MAX_PER_PAGE) {
            @Override
            public String format(Map.Entry<String[], Character> emoji, int index) {
                return MessageUtil.message(" %s &7- &f%s", emoji.getValue(), Joiner.on("&7, &f").join(emoji.getKey()));
            }
        }.display(
            new BukkitWrappedCommandSender(sender),
            EMOJI.entrySet(),
            args.argsLength() == 1 ? args.getInteger(0) : 1
        );
    }
}
