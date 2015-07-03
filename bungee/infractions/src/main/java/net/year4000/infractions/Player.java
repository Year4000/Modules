package net.year4000.infractions;

import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Date;
import java.util.UUID;

@Data
public class Player {
    //private ProxiedPlayer player;
    private String name;
    private UUID uuid;
    private boolean banned = false;
    private boolean locked = false;
    private String lastMessage = "";
    private String time = "";
    private PlayerRecord record;

    public Player(UUID uuid) {
        //this.player = player;
        this.uuid = uuid;

        record = Infractions.getStorage().getPlayer(uuid);
        //Infractions.log(record.toString());
        if (record != null) {
            banned = record.isBanned();
            locked = record.isLocked();
            lastMessage = record.getMessage();
            time = record.getTime();
        }
    }

    public Player(ProxiedPlayer player){
        this(player.getUniqueId());
    }

    public Player(UUID id, String name){
        this(id);
        setName(name);
    }

    public boolean ban(ProxiedPlayer judge, String message) {
        if (isBanned()) return false;
        String date = new Date().toString();
        record.getRecords().add(new InfractionRecord(0, name, judge == null ? "Console" : judge.getName(), message, date, ""));

        Infractions.getStorage().addPlayer(uuid, record);
        return true;
    }

    public boolean lock(ProxiedPlayer judge, String message, int secs) {
        if (isBanned()) return false;
        String date = new Date().toString();
        String length = new Date(System.currentTimeMillis() + (secs * 1000)).toString();

        record.getRecords().add(new InfractionRecord(1, name, judge == null ? "Console" : judge.getName(), message, date, length));
        Infractions.getStorage().addPlayer(uuid, record);

        return true;
    }


    public boolean kick(ProxiedPlayer judge, String message) {
        String date = new Date().toString();
        record.getRecords().add(new InfractionRecord(2, name, judge == null ? "Console" : judge.getName(), message, date, ""));
        Infractions.getStorage().addPlayer(uuid, record);

        return true;
    }
}
