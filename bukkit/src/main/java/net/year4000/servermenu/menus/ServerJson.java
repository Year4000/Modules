package net.year4000.servermenu.menus;

import com.ewized.utilities.core.util.Pinger;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class ServerJson {
    private String name;
    private Group group;
    private Pinger.StatusResponse status;

    @Data
    @AllArgsConstructor
    public static class Group {
        private String name;
        private String display;
    }
}
