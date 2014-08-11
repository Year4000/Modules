package net.year4000.accountlogin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.bungee.module.ModuleListeners;
import net.year4000.ducktape.module.ModuleInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@ModuleInfo(
    name = "AccountLogin",
    version = "1.2",
    description = "Old account system login hacks",
    authors = {"Year4000"}
)
@ModuleListeners({AccountLogin.LoginListener.class})
public class AccountLogin extends BungeeModule {
    private static final String PLUGIN = "[AccountLogin] ";
    private static Configuration config;
    private static Connection connection = null;
    private static String loginip;
    private static String logintime;

    @Override
    public void enable() {
        try {
            config = new Configuration(this);
            config.init();
            connection = DriverManager.getConnection(config.sql_database, config.sql_username, config.sql_password);
        }
        catch (Exception e) {
            System.out.println(PLUGIN + e.getMessage());
        }
    }
    /**
     * Log the user in.
     *
     * @return true|false If the user can be in off line mode.
     */
    private static boolean login(ResultSet rs, String loginip, String logintime, String player) throws Exception {
        // Is the user in the database
        if (rs.first()) {
            String ip = rs.getString(config.accounts_login_ip);
            String time = rs.getString(config.accounts_login_time);

            if (time.substring(0,10).equals(logintime.substring(0,10))) {
                if (ip.equals(loginip)) {
                    System.out.println(PLUGIN + player + " is loggin with offline mode.");
                    return false;
                }
            }
        }
        return true; // User needs to be check with minecraft login servers.
    }

    /**
     * Run checks on the result set on the user.
     */
    private static void check(ResultSet rs) throws Exception {
        // Is the user in the database
        if (rs.first()) {
            String status = rs.getString(config.accounts_login_status);

            // Is the user activated
            if (status.equalsIgnoreCase(config.accounts_login_activate)) {
                //throw new Exception(config.message_activate);
            }

            // Is the user banned
            if (status.equalsIgnoreCase(config.accounts_login_banned)) {
                throw new Exception(config.message_banned);
            }
        }
        else {
            // Tell the user to register.
            //throw new Exception(config.message_register);
        }
    }

    public static class LoginListener implements Listener {
        /**
         * Set the max player count to the size of the accounts.
         */
        @EventHandler
        public void onPing(ProxyPingEvent event) {
            // If not enabled return.
            if (!config.accounts_playercount) return;

            // Set the max player count to the number of accounts.
            try {
                // Setup the vars.
                ServerPing ping = event.getResponse();
                String sql = "SELECT `"+config.accounts_user+"` FROM `"+config.accounts_table+"`";
                Statement statement = connection.createStatement();
                ResultSet resultset = statement.executeQuery(sql);
                int maxPlayers = 0;
                while (resultset.next()) maxPlayers++;

                // Set the size.
                ping.getPlayers().setMax(maxPlayers);
            }
            catch (Exception e) {}
        }

        /**
         * Check the user with the account.
         */
        @EventHandler
        public void onLogin(PreLoginEvent event) {
            String player = event.getConnection().getName();
            String sql = "SELECT * FROM `"+config.accounts_table+"` WHERE `"+config.accounts_user+"`='"+player+"'";
            logintime = new java.sql.Timestamp(System.currentTimeMillis()).toString();
            loginip = event.getConnection().getAddress().getAddress().toString().substring(1);

            try {
                Statement statement = connection.createStatement();
                ResultSet resultset = statement.executeQuery(sql);

                // Check the user
                check(resultset);

                // Log the user in
                event.getConnection().setOnlineMode(login(resultset, loginip, logintime, player));
            }
            catch (Exception e) {
                String color = ChatColor.translateAlternateColorCodes('&', e.getMessage());
                System.out.println(PLUGIN + player + " : " + color);
                event.setCancelReason(color);
                event.setCancelled(true);
            }
        }

        /**
         * Update last ip after player has logedin
         */
        @EventHandler
        public void onJoin(PostLoginEvent event) {
            String player = event.getPlayer().getName();
            String updateSQL = "UPDATE `"+config.accounts_table+"`";
            updateSQL += "SET `"+config.accounts_login_time+"`='"+logintime+"',`"+config.accounts_login_ip+"`='"+loginip+"'";
            updateSQL += "WHERE `"+config.accounts_user+"`='"+player+"'";
            try {
                Statement statement = connection.createStatement();

                // Update the ip for the user
                statement.execute(updateSQL);
            }
            catch (Exception e) {}
        }
    }
}