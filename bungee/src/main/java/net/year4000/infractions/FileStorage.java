package net.year4000.infractions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FileStorage {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final File STORAGE = new File(Infractions.getInstance().getDataFolder(), "storage.json");
    private Storage db = new Storage();

    public FileStorage() {
        if (!STORAGE.exists()) {
            write(STORAGE);
        }

        read(STORAGE);
    }

    public void write(File storage) {
        try (FileWriter file = new FileWriter(storage)) {
            file.write(gson.toJson(db));
        } catch (IOException e) {
            Infractions.log(e, false);
        }
    }

    public void read(File storage) {
        try {
            Reader file = new FileReader(storage);
            db = gson.fromJson(file, Storage.class);
        } catch (FileNotFoundException e) {
            Infractions.log(e, false);
        }

    }

    public PlayerRecord getPlayer(UUID uuid) {
        //return players.containsKey(player.getUniqueId().toString()) ? players.get(player.getUniqueId().toString()) : new PlayerRecord();
        if (db.getPlayers().containsKey(uuid.toString())) {
            PlayerRecord rec = new PlayerRecord();
            rec.setRecords(db.getPlayers().get(uuid.toString()));
            return rec;
        }
        else {
            return new PlayerRecord();
        }
    }

    // replace the record
    public void addPlayer(UUID uuid, PlayerRecord record) {
        db.getPlayers().put(uuid.toString(), record.getRecords());

        write(STORAGE);
    }

    public PlayerRecord getPlayer(ProxiedPlayer player) {
        return getPlayer(player.getUniqueId());
    }

    // replace the record
    public void addPlayer(ProxiedPlayer player, PlayerRecord record) {
        addPlayer(player.getUniqueId(), record);
    }

    public void saveUUID(ProxiedPlayer player){
        db.getPlayerUuids().put(player.getName().toLowerCase(), player.getUniqueId());

        write(STORAGE);
    }

    public boolean hasUUID(String name) {
        return db.getPlayerUuids().containsKey(name.toLowerCase());
    }

    public UUID getUUID(String name) {
        if(!hasUUID(name))
            return null;
        return db.getPlayerUuids().get(name.toLowerCase());
    }

    @Data
    private class Storage {
        private HashMap<String, List<InfractionRecord>> players = new HashMap<>();
        private HashMap<String, UUID> playerUuids = new HashMap<>();

    }
}
