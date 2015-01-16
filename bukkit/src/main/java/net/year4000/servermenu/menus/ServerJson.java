package net.year4000.servermenu.menus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.utilities.Pinger;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public final class ServerJson {
    private String name;
    private Group group;
    private Pinger.StatusResponse status;

    /** Is this server hidden */
    public boolean isHidden() {
        return name.startsWith(".") || getGroup().isHidden();
    }

    @Data
    @AllArgsConstructor
    public static class Group {
        private String name;
        private String display;

        /** Is this server hidden */
        public boolean isHidden() {
            return name.startsWith(".");
        }
    }

    @Data
    @NoArgsConstructor
    public static class Count {
        private int online = 0;
        private int max = 0;
    }

    @Data
    @NoArgsConstructor
    public static class PlayerCount {
        private Count network = new Count();
        private Map<String, Count> groups = new HashMap<>();
    }
}
