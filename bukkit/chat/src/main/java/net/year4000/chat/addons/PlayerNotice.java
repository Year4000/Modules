/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.chat.addons;

import net.year4000.chat.events.MessageReceiveEvent;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerNotice implements Listener {
    private static final int MIN_SIZE = 3;

    @EventHandler
    public void onSent(MessageReceiveEvent event) {
        event.setSend((player, data, message) -> {
            if (checkName(data.getActorName(), player.getName(), message)) {
                FunEffectsUtil.playSound(player, Sound.NOTE_PLING);
                // Change the mention to current mention
                String word = getWord(data.getActorName(), player.getName(), message);

                return message.replaceAll(word, MessageUtil.message("&b@%s&r", player.getName()));
            }
            else {
                return message;
            }
        });
    }

    /** Checks if a word in the string matches the player */
    private String getWord(String player, String sender, String msg) {
        for (String word : msg.split(" ")) {
            if (word.length() > MIN_SIZE && word.length() <= sender.length()) {
                word = word.toLowerCase();
                String shortSender = sender.substring(0, word.length() - 1);

                if (word.startsWith(shortSender.toLowerCase())) {
                    if (!player.contains(word)) {
                        return word;
                    }
                }
            }
        }

        return null;
    }

    /** Checks if a word in the string matches the player */
    private boolean checkName(String player, String sender, String msg) {
        for (String word : msg.split(" ")) {
            if (word.length() > MIN_SIZE && word.length() <= sender.length()) {
                word = word.toLowerCase();
                String shortSender = sender.substring(0, word.length()-1);

                if (word.startsWith(shortSender.toLowerCase())) {
                    if (!player.contains(word)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
