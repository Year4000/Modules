package net.year4000.vanish;

import com.ewized.utilities.bukkit.util.MessageUtil;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.year4000.vanish.messages.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class VanishCommands {
    @Command(
        aliases = {"vanish", "hide", "show", "v"},
        desc = "Hide your self from others."
    )
    public static void vanish(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) throw new CommandException("Must be a player!");

        Player player = (Player) sender;

        if (Vanish.hidden.containsKey(player)) {
            player.setDisplayName(Vanish.hidden.get(player));
            Vanish.hidden.remove(player);
            Vanish.updateHidden();
            sender.sendMessage(new Message(sender).get("vanish.show"));
        }
        else {
            Vanish.hidden.put(player, player.getDisplayName());
            player.setDisplayName(MessageUtil.replaceColors("&8&m" + player.getName() + "&r"));
            Vanish.updateHidden();
            sender.sendMessage(new Message(sender).get("vanish.hidden"));
        }
    }
}
