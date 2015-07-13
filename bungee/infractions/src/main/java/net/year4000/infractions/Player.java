/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.infractions;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.year4000.utilities.sdk.routes.accounts.AccountRoute;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
public class Player {
    private String name;
    private UUID uuid;
    private String locale = Locale.US.toString();
    private boolean banned = false;
    private boolean locked = false;
    private String lastMessage = "";
    private int time = -1;
    private PlayerRecord record;

    public Player(AccountRoute account) {
        this.uuid = UUID.fromString(account.getUUID());
        this.name = account.getUsername();

        /*record = Infractions.getStorage().getPlayer(uuid);*/

        JsonElement infractions = account.getRawResponse().get("infractions");

        if (infractions != null) {
            PlayerRecord record = new PlayerRecord();
            List<InfractionRecord> records = Lists.newLinkedList();
            JsonArray array = infractions.getAsJsonArray();
            Gson gson = new Gson();

            for (JsonElement element : array) {
                records.add(gson.fromJson(element, InfractionRecord.class));
            }

            record.setRecords(records);

            this.record = record;
        }

        if (record != null) {
            banned = record.isBanned();
            locked = record.isLocked();
            lastMessage = record.getMessage();
            time = record.getTime();
        }
    }

    public boolean ban(ProxiedPlayer judge, String message) {
        if (isBanned()) return false;
        int time = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        Infractions.getStorage().addRecord(uuid, new InfractionRecord("ban", judge, message, time, null));
        return true;
    }

    public boolean lock(ProxiedPlayer judge, String message, int secs) {
        if (isBanned()) return false;
        int time = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        int length = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + secs;

        Infractions.getStorage().addRecord(uuid, new InfractionRecord("lock", judge, message, time, length));
        return true;
    }


    public boolean kick(ProxiedPlayer judge, String message) {
        int time = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        Infractions.getStorage().addRecord(uuid, new InfractionRecord("kick", judge, message, time, null));

        return true;
    }
}
