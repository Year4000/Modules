/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.linker;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import net.year4000.utilities.Pinger;
import net.year4000.utilities.sdk.routes.Route;

import java.util.Map;

public class ServerRoute extends Route<Map<String, ServerRoute.ServerJsonKey>> {
    /** Get a immutable version of the map */
    public ImmutableMap<String, ServerJsonKey> getServersMap() {
        return new ImmutableMap.Builder<String, ServerJsonKey>().putAll(response).build();
    }

    /** Get a collection of all the ServerJson objects returned by this route */
    public ImmutableCollection<ServerJsonKey> getServersCollection() {
        return new ImmutableList.Builder<ServerJsonKey>().addAll(response.values()).build();
    }

    /** Get a server object by its name */
    public ServerJsonKey getServer(@NonNull String server) {
        return response.get(server);
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public class ServerJsonKey {
        private String hostname;
        private Integer port;
        private String name;
        private Group group;
        private Pinger.StatusResponse status;

        /** Is this server hidden */
        public boolean isHidden() {
            return name.startsWith(".") || getGroup().isHidden();
        }

        @Value
        public class Group {
            private String name;
            private String display;

            /** Is this server hidden */
            public boolean isHidden() {
                return name.startsWith(".");
            }
        }
    }
}
