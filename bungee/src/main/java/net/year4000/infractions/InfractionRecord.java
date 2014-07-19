package net.year4000.infractions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InfractionRecord {
    private int type; /* 0 = Banned | 1 = lock | 2 = kick */
    private String player;
    private String judge;
    private String message;
    private String date;
    private String time;
}
