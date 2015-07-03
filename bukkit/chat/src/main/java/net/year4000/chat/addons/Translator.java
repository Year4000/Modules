/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.chat.addons;

import lombok.NonNull;
import net.year4000.chat.Chat;
import net.year4000.chat.LocaleManager;
import net.year4000.chat.events.MessageReceiveEvent;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.commands.Command;
import net.year4000.utilities.bukkit.commands.CommandContext;
import net.year4000.utilities.bukkit.commands.CommandException;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator implements Listener {
    private static List<String> toNotTranslate = new ArrayList<>();
    private GoogleTranslator translator = new GoogleTranslator();

    public Translator() {
        Chat.get().registerCommand(Translator.class);
    }

    public static boolean hasTranslatorEnabled(Player p) {
        return !toNotTranslate.contains(p.getName());
    }

    @EventHandler
    public void onSent(MessageReceiveEvent event) {
        boolean globalTranslate = event.getMessage().getMessage().startsWith("|");
        if (globalTranslate) {
            event.getMessage().setMessage(event.getMessage().getMessage().substring(1));
        }
        event.setSend((player, data, message) -> {
            String msg = data.getMessage();
            String translation = "";
            if (player.getName().equalsIgnoreCase(data.getActorName())) {
                return message;
            }
            else if (globalTranslate || hasTranslatorEnabled(player)) {
                translation = translate(msg, player);
            }
            else {
                return message;
            }

            return message.replace(msg, translation);
        });
    }

    private String translate(String message, Player player) {
        return translator.getTranslation(message, player.spigot().getLocale().split("_")[0]);
    }

    @Command(
        aliases = {"translator", "toggletranslate", "tt"},
        desc = "Toggle translating of chat messages"
    )
    public static void toggle(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(LocaleManager.get().getLocale("en_US").get("translator.noconsole").toString());
        }
        Player p = (Player)sender;
        if (hasTranslatorEnabled(p)) {
            toNotTranslate.add(p.getName());
            sender.sendMessage(MessageUtil.message(LocaleManager.get().getLocale(p.spigot().getLocale()).get("translator.disabled").toString()));
        }
        else {
            toNotTranslate.remove(p.getName());
            sender.sendMessage(MessageUtil.message(LocaleManager.get().getLocale(p.spigot().getLocale()).get("translator.enabled").toString()));
        }
    }

    private class GoogleTranslator {
        private Map<String, Translation> cache = new HashMap<>();
        private final Pattern p = Pattern.compile("(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?������]))");

        public String getTranslationOf(@NonNull final String string, @NonNull final String fromLang, @NonNull final String toLang) throws IOException {
            return getTranslation(string, toLang);
        }

        private String readURL(final String url) throws IOException {
            final StringBuilder response = new StringBuilder();
            final URL toRead = new URL(url);
            final URLConnection yc = toRead.openConnection();
            // Yahoo uses this UserAgent, so might as well use it to prevent 403s
            yc.setRequestProperty("User-Agent", "Mozilla/5.0");
            final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }

        private String getTranslation(String text, final String lang) {
            final Map<String, String> hm = new HashMap<>();
            final Matcher m = p.matcher(text);
            final StringBuffer sb = new StringBuffer();
            String urlTmp;
            // URL handling
            while (m.find()) {
                urlTmp = m.group(1);
                final String uuid = UUID.randomUUID().toString().replace("-", "");
                hm.put(uuid, urlTmp);
                text = text.replace(urlTmp, uuid);
                m.appendReplacement(sb, "");
                sb.append(urlTmp);
            }
            m.appendTail(sb);
            text = sb.toString();
            // end replace with UUID
            text = URLEncoder.encode(text);

            if (cache.containsKey(text) && cache.get(text).hasTranslation(lang)) {
                return cache.get(text).getTranslation(lang);
            }

            String response = "";
            try {
                response = parse(readURL("http://translate.google.com/translate_a/t?q=" + text + "&client=p&text=&sl=auto&tl=" + lang + "&ie=UTF-8&oe=UTF-8"));
            }
            catch (IOException | ParseException ex) {
                return text;
            }

            // begin UUID to URL
            final Set<Map.Entry<String, String>> set = hm.entrySet();
            for (final Map.Entry<String, String> me : set) {
                response = response.replace(me.getKey(), me.getValue());
            }
            // end UUID to URL
            response = postProcess(response, lang);

            if (cache.containsKey(text)) {
               cache.get(text).storeTranslation(lang, response);
            }
            else {
                cache.put(text, new Translation(text, response, lang));
            }

            return response;
        }

        private String postProcess(String response, final String lang) {
            // post processing text
            response = response.replace(" :", ":");
            response = response.replace(" ,", ",");
            response = response.replace(". / ", "./");

            if (response.startsWith("\u00BF") && StringUtils.countMatches(response, "?") == 0) {
                response = response + "?";
            }
            if (response.startsWith("\u00A1") && StringUtils.countMatches(response, "!") == 0) {
                response = response + "!";
            }
            if (lang.equals("en") && response.startsWith("'re")) {
                response = "You" + response;
            }
            return response;
        }

        private String parse(final String response) throws ParseException {
            final JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(response);
            final JSONArray sentences = (JSONArray) obj.get("sentences");
            String finalResponse = "";
            for (final Object sentence : sentences) {
                final String line = "" + sentence;
                final String trans = getTrans(line);
                finalResponse = finalResponse + trans;
            }
            return finalResponse;
        }

        private String getTrans(final String sentence) throws ParseException {
            final JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(sentence);
            return (String) obj.get("trans");
        }
    }

    private class Translation {
        private Map<String, String> store = new HashMap<>();
        private String original;

        public Translation(String original, String firstResult, String firstLang) {
            this.original = original;
            store.put(firstLang, firstResult);
        }

        public boolean hasTranslation(String language) {
            return store.containsKey(language);
        }

        public String getTranslation(String language) {
            if (!hasTranslation(language)) {
                return original;
            }
            else {
                return store.get(language);
            }
        }

        public void storeTranslation(String language, String result) {
            store.put(language, result);
        }
    }
}
