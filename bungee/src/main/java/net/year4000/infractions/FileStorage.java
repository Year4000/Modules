package net.year4000.infractions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.*;
import java.util.*;

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

    @SuppressWarnings("unchecked")
    public void read(File storage) {
        try {
            Reader file = new FileReader(storage);
            db = gson.fromJson(file, Storage.class);
        } catch (FileNotFoundException e) {
            Infractions.log(e, false);
        }

    }

    public PlayerRecord getPlayer(ProxiedPlayer player) {
        //return players.containsKey(player.getUniqueId().toString()) ? players.get(player.getUniqueId().toString()) : new PlayerRecord();
        if (db.getPlayers().containsKey(player.getUniqueId().toString())) {
            PlayerRecord rec = new PlayerRecord();
            rec.setRecords(db.getPlayers().get(player.getUniqueId().toString()));
            return rec;
        }
        else {
            return new PlayerRecord();
        }
    }

    // replace the record
    public void addPlayer(ProxiedPlayer player, PlayerRecord record) {
        db.getPlayers().put(player.getUniqueId().toString(), record.getRecords());

        write(STORAGE);
    }

    @Data
    private class Storage {
        private HashMap<String, List<InfractionRecord>> players = new HashMap<>();

    }
}
