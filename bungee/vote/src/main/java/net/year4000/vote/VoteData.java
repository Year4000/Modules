package net.year4000.vote;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class VoteData {
    /** Account mongo id */
    private String id;
    /** The player uuid */
    private String uuid;
    /** The vote object returned */
    private Votes vote;

    @Getter
    @ToString
    @EqualsAndHashCode
    public class Votes {
        /** The service name of the vote data */
        @SerializedName("service_name")
        private String serviceName;
        /** The username for the vote */
        private String username;
        /** The ip address of the player who voted */
        private String address;
        /** The time stamp of the vote, usually in unix time but not always */
        @SerializedName("time_stamp")
        private String timeStamp;
    }
}
