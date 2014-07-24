package net.year4000.infractions;

import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Date;
import java.util.UUID;

@Data
public class Player {
    private ProxiedPlayer player;
    private UUID uuid;
    private boolean banned = false;
    private boolean locked = false;
    private String lastMessage = "";
    private String time = "";
    private PlayerRecord record;

    public Player(ProxiedPlayer player) {
        this.player = player;
        uuid = player.getUniqueId();

        record = Infractions.getStorage().getPlayer(player);
        //Infractions.log(record.toString());
        if (record != null) {
            banned = record.isBanned();
            locked = record.isLocked();
            lastMessage = record.getMessage();
            time = record.getTime();
        }
    }

    public boolean ban(ProxiedPlayer judge, String message) {
        if (isBanned()) return false;
        String date = new Date().toString();
        record.getRecords().add(new InfractionRecord(0, player.getName(), judge == null ? "Console" : judge.getName(), message, date, ""));

        Infractions.getStorage().addPlayer(player, record);
        return true;
    }

    public boolean lock(ProxiedPlayer judge, String message, String length) {
        if (isBanned()) return false;
        String date = new Date().toString();
        length = new Date(System.currentTimeMillis() + Integer.valueOf(length)).toString();

        record.getRecords().add(new InfractionRecord(1, player.getName(), judge == null ? "Console" : judge.getName(), message, date, length));
        Infractions.getStorage().addPlayer(player, record);

        return true;
    }


    public boolean kick(ProxiedPlayer judge, String message) {
        String date = new Date().toString();
        record.getRecords().add(new InfractionRecord(2, player.getName(), judge == null ? "Console" : judge.getName(), message, date, ""));
        Infractions.getStorage().addPlayer(player, record);

        return true;
    }
}
