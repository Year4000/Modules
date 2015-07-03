/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.serverlist.messages;

import lombok.Data;
import net.year4000.serverlist.Settings;
import net.year4000.utilities.cache.QuickCache;

import java.util.*;

@Data
public class MessageFactory {
    /** When the instance is created set the following vars. */
    private static QuickCache<MessageFactory> inst = QuickCache.builder(MessageFactory.class).build();
    private static Settings config = Settings.get();
    private final Random rand = new Random();
    private final Date date;

    /** The list that will contain the messages for today. */
    private List<String> messages = new ArrayList<>();

    /**
     *  When this instant is created fill the messages list with messages
     *  that are for the date, when this instance is created at.
     */
    public MessageFactory() {
        date = new Date();
        parse();
    }

    /** Create messages on the specific date only used for testing. */
    public MessageFactory(Date date) {
        this.date = date;
        config = new Settings(true);
        parse();
    }

    public static MessageFactory get() {
        return inst.get();
    }

    private void parse() {
        // Cycle through the config's list
        config.getMessages().entrySet().forEach(list -> {
            // the name of the list should look like */*/*
            list.getKey();

            // TODO Depending on the date run some checks to see if that
            // TODO should add the contents of that list to the messagefactory list

            // string list will be added to the list
            list.getValue().forEach(message -> messages.add(message));
        });
    }

    /** Get a random message for today. */
    public String getMessage() {
        // Shuffle the list.
        Collections.shuffle(messages);

        // Return a random message from the list.
        return messages.get(rand.nextInt(messages.size()));
    }
}
