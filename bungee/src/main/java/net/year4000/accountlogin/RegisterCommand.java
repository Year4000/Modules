package net.year4000.accountlogin;

import com.google.gson.Gson;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.utilities.bungee.MessageUtil;
import net.year4000.utilities.bungee.commands.Command;
import net.year4000.utilities.bungee.commands.CommandContext;
import net.year4000.utilities.bungee.commands.CommandException;
import sun.misc.IOUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Statement;

public class RegisterCommand {
    @Command(
        aliases = {"register"},
        usage = "[email] [password]",
        desc = "Change your email or password to your account",
        min = 2
    )
    public static void register(CommandContext args, CommandSender sender) throws CommandException {
        if (sender instanceof ProxiedPlayer) {
            String name = sender.getName();
            String email = args.getString(0);
            String password = password(args.getString(1));

            String updateSQL = "UPDATE `accounts`";
            updateSQL += "SET `email`='"+email+"',`password`='"+password+"'";
            updateSQL += "WHERE `username`='"+name+"'";
            try {
                Statement statement = AccountLogin.connection.createStatement();

                // Update the ip for the user
                statement.execute(updateSQL);
                sender.sendMessage(MessageUtil.message("&6You have updated your email and password you may login."));
            }
            catch (Exception e) {
                throw new CommandException("Could not update email and password, report at github.com/Year4000/Meta");
            }
        }
    }

    public static String password(String password) {
        try {
            InputStream result = new URI(String.format("https://www.year4000.net/hash/%s/", password)).toURL().openStream();

            return new Gson().fromJson(new InputStreamReader(result), Hash.class).hash;
        } catch (Exception e) {}
        return null;
    }
}
