/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.vote;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.year4000.ducktape.bungee.DuckTape;
import net.year4000.ducktape.bungee.module.BungeeModule;
import net.year4000.ducktape.module.ModuleInfo;
import net.year4000.utilities.AccountBadgeManager;
import net.year4000.utilities.bungee.MessageUtil;
import net.year4000.utilities.redis.RedisMessaging;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleInfo(
    name = "Vote",
    version = "1.0.0",
    description = "Voting for in game effects",
    authors = {"Year4000"}
)
public class Vote extends BungeeModule {
    private static final HostAndPort HOST_AND_PORT = new HostAndPort("internal.api.year4000.net", 6379);
    private static final String CHANNEL = "year4000.votifier.vote";
    private ProxyServer proxy = ProxyServer.getInstance();
    private VoteSettings vote = VoteSettings.get();
    private JedisPool pool;
    private ScheduledTask task;
    private AccountBadgeManager badges = new AccountBadgeManager();

    @Override
    public void enable() {
        // Run messaging listener in its own thread
        pool = new JedisPool(HOST_AND_PORT.getHost(), HOST_AND_PORT.getPort());
        RedisMessaging messaging = new RedisMessaging(pool);
        task = proxy.getScheduler().runAsync(DuckTape.get(), messaging::init);

        // Subscribe the listener to the channel
        messaging.subscribe(CHANNEL, (data) -> {
            debug("Message Input: " + data);
            VoteData object = new Gson().fromJson(data, VoteData.class);
            processData(object);
        });
    }

    @Override
    public void disable() {
        // Cancel the thread and close redis
        task.cancel();
        pool.close();
    }

    /** Process the vote data */
    private void processData(VoteData data) {
        Optional<VoteSettings.Service> service = vote.getService(data.getVote().getServiceName());
        debug("Service: " + service.get());

        if (service.isPresent()) {
            // Message to players except self
            proxy.getPlayers()
            .stream()
            .filter(player -> !player.getUniqueId().toString().equals(data.getUuid()))
            .forEach(player -> {
                BaseComponent[] message = makeMessage(player, data, service.get());
                player.sendMessage(ChatMessageType.CHAT, message);
            });

            // Message to voter
            Optional<ProxiedPlayer> player = Optional.ofNullable(proxy.getPlayer(UUID.fromString(data.getUuid())));
            player.ifPresent(user -> {
                BaseComponent[] message = makeMessage(user, service.get());
                user.sendMessage(ChatMessageType.CHAT, message);
            });

            // Message to console
            CommandSender sender = proxy.getConsole();
            BaseComponent[] message = makeMessage(sender, data, service.get());
            debug("Vote: " + sender.getName() + " > " + Joiner.on(" ").join(message));
        }
    }

    /** Make messge for self */
    private BaseComponent[] makeMessage(CommandSender player, VoteSettings.Service service) {
        ComponentBuilder builder = new ComponentBuilder(VoteMessage.Y4K_VOTE_RECIVED.translate(player) + " ")
            .color(ChatColor.AQUA)
            .append(service.getName())
            .color(ChatColor.DARK_AQUA)
            .append(". ")
            .color(ChatColor.AQUA)
            .append(VoteMessage.Y4K_VOTE_REWARDS.translate(player))
            .color(ChatColor.AQUA)
            .append(": ")
            .color(ChatColor.GRAY)
            ;

        AtomicInteger count = new AtomicInteger(1);
        int size = vote.getRewards().size();
        vote.getRewards().forEach((name, value) -> {
            builder
                .append(value + " ")
                .color(ChatColor.DARK_GREEN)
                .append(name)
                .color(ChatColor.GREEN)
                ;

            if (count.getAndIncrement() != size) {
                builder
                    .append(", ")
                    .color(ChatColor.GRAY)
                ;
            }
        });

        return builder.create();
    }

    /** The message to send to all the online players */
    private BaseComponent[] makeMessage(CommandSender player, VoteData data, VoteSettings.Service service) {
        BaseComponent[] hover = new ComponentBuilder(VoteMessage.Y4K_VOTE_CLICK.translate(player))
            .color(ChatColor.DARK_AQUA)
            .append(" " + service.getService())
            .color(ChatColor.AQUA)
            .append("!")
            .color(ChatColor.DARK_AQUA)
            .create();

        BaseComponent[] badge = TextComponent.fromLegacyText(badges.getBadge(data.getId()));

        return MessageUtil.merge(badge, new ComponentBuilder(" " + data.getVote().getUsername())
            .color(ChatColor.GREEN)
            .append(" " + VoteMessage.Y4K_VOTE_CHAT.translate(player) + " ")
            .color(ChatColor.GOLD)
            .append(service.getName())
            .color(ChatColor.GREEN)
            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, service.getUrl()))
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
            .append("!")
            .color(ChatColor.GOLD)
            .create());
    }
}
