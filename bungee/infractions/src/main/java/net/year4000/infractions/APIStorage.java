package net.year4000.infractions;

import com.google.gson.JsonObject;
import net.year4000.utilities.sdk.API;
import net.year4000.utilities.sdk.HttpConnection;
import net.year4000.utilities.sdk.HttpFetcher;

import java.util.Optional;
import java.util.UUID;

public class APIStorage {
    private static final String url = "https://api.year4000.net/infractions/";
    private API api = new API(System.getenv("Y4K_KEY"));

    public Optional<Player> getPlayer(String id) {
        try {
            return Optional.ofNullable(new Player(api.getAccount(id)));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }


    /** Add the record to the database */
    public void addRecord(UUID player, InfractionRecord record) {
        String uri = url + player.toString();
        HttpConnection connection = new HttpConnection(uri);
        connection.getHeaders().put("Authorization", "Key " + System.getenv("Y4K_KEY"));
        JsonObject jsonObject = record.toJson();
        HttpFetcher.post(connection, jsonObject, JsonObject.class, (d, e) -> {});
    }
}
