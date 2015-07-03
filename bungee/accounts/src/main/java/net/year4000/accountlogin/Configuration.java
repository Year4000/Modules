package net.year4000.accountlogin;

import net.year4000.utilities.config.Config;

import java.io.File;

public class Configuration extends Config {
    public Configuration(AccountLogin plugin) {
        CONFIG_HEADER = new String[]{"AccountLogin Configuration"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "config.yml");
    }

    public String sql_database = "jdbc:mysql://localhost:3306/database?autoReconnect=true";
    public String sql_username = "root";
    public String sql_password = "P@ssw0rd";

    public boolean accounts_playercount = true;
    public String accounts_table = "accounts";
    public String accounts_user = "username";
    public String accounts_login_time = "login_time";
    public String accounts_login_ip = "login_ip";
    public String accounts_login_status = "status";
    public String accounts_login_banned = "BANNED";
    public String accounts_login_activate = "ACTIVATE";

    public String message_activate = "&eYour account is not activated.";
    public String message_banned = "&cYour account is banned.";
    public String message_register = "&eYou are not registered on &6www.year4000.net";
}
