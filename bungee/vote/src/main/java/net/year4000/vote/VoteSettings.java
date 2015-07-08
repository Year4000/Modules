package net.year4000.vote;


import lombok.*;
import net.year4000.utilities.configs.Config;
import net.year4000.utilities.configs.ConfigURL;

import java.util.Map;
import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigURL(value = "https://api.year4000.net/configs/votes", config = VoteSettings.class)
public class VoteSettings extends Config<VoteSettings> {
    private static VoteSettings inst;

    /** The services that one can vote at */
    @Getter(AccessLevel.NONE)
    private Map<String, Service> services;

    /** Get the service of the vote */
    public Optional<Service> getService(String name) {
        return Optional.ofNullable(services.get(name));
    }

    /** Get the instance of this object */
    public static VoteSettings get() {
        if (inst == null) {
            VoteSettings settings = new VoteSettings();
            inst = settings.getInstance(settings);
        }

        return inst;
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    class Service {
        private String service;
        private String name;
        private String url;
    }
}
