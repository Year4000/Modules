package net.year4000.infractions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class PlayerRecord {
    private List<InfractionRecord> records = new ArrayList<>();
    private String message = "";
    private String time = "";

    public boolean isBanned() {
        for (InfractionRecord r : records) {
            if (r.getType() == 0) {
                message = r.getMessage();
                time = r.getTime();
                return true;
            }
        }

        return false;
    }

    public boolean isLocked() {
        for (InfractionRecord r : records) {
            if (r.getType() == 1) {
                message = r.getMessage();
                time = r.getTime();
                return new Date().before(new Date(time));
            }
        }

        return false;
    }
}
