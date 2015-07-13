/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.infractions;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InfractionRecord {
    private String type; /* 0 = Banned | 1 = lock | 2 = kick */
    private Map<String, String> judge;
    private String message;
    private int time;
    private Integer expires;

    public InfractionRecord(String type, ProxiedPlayer judge, String message, int time, Integer expires) {
        this.type = type;
        this.judge = Maps.newHashMap();
        this.judge.put("username", judge.getName());
        this.judge.put("uuid", judge.getUniqueId().toString());
        this.message = message;
        this.time = time;
        this.expires = expires;
    }

    public JsonObject toJson() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this, InfractionRecord.class), JsonObject.class);
    }
}
