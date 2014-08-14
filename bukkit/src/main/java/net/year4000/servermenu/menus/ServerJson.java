package net.year4000.servermenu.menus;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.year4000.utilities.Pinger;

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
}
