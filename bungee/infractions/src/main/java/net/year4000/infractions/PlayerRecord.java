/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.infractions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class PlayerRecord {
    private List<InfractionRecord> records = new LinkedList<>();
    private String message = "";
    private int time = -1;

    public boolean isBanned() {
        for (InfractionRecord r : records) {
            if (r.getType().equals("ban")) {
                message = r.getMessage();
                time = r.getTime();
                return true;
            }
        }

        return false;
    }

    public boolean isLocked() {
        List<InfractionRecord> list = new LinkedList<>(records);
        Collections.reverse(list);

        for (InfractionRecord r : list) {
            if (r.getType().equals("lock")) {
                message = r.getMessage();
                time = r.getTime();
                return new Date().before(new Date(time));
            }
        }

        return false;
    }
}
