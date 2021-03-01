/*
 * Copyright (C) 2019  OopsieWoopsie
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package eu.mcdb.ban_announcer;

import static eu.mcdb.ban_announcer.PunishmentAction.Type.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
import eu.mcdb.ban_announcer.addon.BanAnnouncerAddon;
import eu.mcdb.ban_announcer.config.Config;
import eu.mcdb.ban_announcer.config.Messages;
import org.spicord.Spicord;
import eu.mcdb.spicord.bot.DiscordBot;
import eu.mcdb.spicord.embed.Embed;
import eu.mcdb.spicord.embed.EmbedSender;
import lombok.Getter;
import net.dv8tion.jda.core.entities.MessageChannel;

public final class BanAnnouncer {

    @Getter private static BanAnnouncer instance;

    @Getter private Config config;
    @Getter private Logger logger;
    @Getter private boolean enabled = true;

    private Map<PunishmentAction.Type, Function<PunishmentAction, Embed>> callbacks;
    private Set<DiscordBot> bots;

    public BanAnnouncer(Config config, Logger logger) {
        instance = this;

        this.config = config;
        this.logger = logger;

        this.bots = new HashSet<>();
        this.callbacks = new HashMap<>();

        BiFunction<PunishmentAction, Embed, Embed> builder;

        builder = (punishment, template) -> new MessageFormatter()
                .setString("player", punishment.getPlayer())
                .setString("staff", punishment.getOperator())
                .setString("reason", punishment.getReason())
                .setString("duration", punishment.getDuration())
                .setString("time", punishment.getTime())
                .setString("date", punishment.getDate())
                .format(template);

        Messages messages = config.getMessages();

        callbacks.put(BAN,     p -> builder.apply(p, p.isPermanent() ? messages.getBan() : messages.getTempban()));
        callbacks.put(BANIP,   p -> builder.apply(p, p.isPermanent() ? messages.getBanip() : messages.getTempbanip()));
        callbacks.put(MUTE,    p -> builder.apply(p, p.isPermanent() ? messages.getMute() : messages.getTempmute()));
        callbacks.put(WARN,    p -> builder.apply(p, p.isPermanent() ? messages.getWarn() : messages.getTempwarn()));
        callbacks.put(KICK,    p -> builder.apply(p, messages.getKick()));
        callbacks.put(UNBAN,   p -> builder.apply(p, messages.getUnban()));
        callbacks.put(UNBANIP, p -> builder.apply(p, messages.getUnbanip()));
        callbacks.put(UNMUTE,  p -> builder.apply(p, messages.getUnmute()));
        callbacks.put(UNWARN,  p -> builder.apply(p, messages.getUnwarn()));

        callbacks.put(TEMPBAN,   callbacks.get(BAN));
        callbacks.put(TEMPBANIP, callbacks.get(BANIP));
        callbacks.put(TEMPMUTE,  callbacks.get(MUTE));
        callbacks.put(TEMPWARN,  callbacks.get(WARN));

        Spicord.getInstance().getAddonManager().registerAddon(new BanAnnouncerAddon(this));
    }

    public void handlePunishmentAction(PunishmentAction punishment) {
        if (!enabled) {
            logger.warning("BanAnnouncer is not enabled, ignoring punishment.");
            logger.warning(punishment.toString());
            return;
        }

        Embed embed = callbacks.get(punishment.getType()).apply(punishment);
        sendDiscordMessage(embed);
    }

    private void sendDiscordMessage(Embed message) {
        if (message == null) {
            System.out.println("Message is null, ignoring it.");
            return;
        }

        bots.stream().filter(DiscordBot::isReady).map(DiscordBot::getJda).forEach(jda -> {
            config.getChannelsToAnnounce().forEach(channelId -> {
                MessageChannel channel = jda.getTextChannelById(channelId);

                if (channel == null) {
                    logger.severe("Cannot find the channel with id '" + channelId + "'. The message was not sent.");
                } else {
                    EmbedSender.prepare(channel, message).queue(success -> {
                        logger.info("The punishment message was sent.");
                    }, fail -> {
                        logger.warning("Couldn't send the punishment message: " + fail.getMessage());
                    });
                }
            });
        });
    }

    public void addBot(DiscordBot bot) {
        bots.add(bot);
    }

    public void disable() {
        callbacks.clear();
        bots.clear();
        enabled = false;
        instance = null;
        config = null;
        callbacks = null;
        bots = null;
    }

    private class MessageFormatter {

        private final Map<String, String> map;
        private final char c;

        public MessageFormatter(char c) {
            this.map = new HashMap<String, String>();
            this.c = c;
        }

        public MessageFormatter() {
            this('%');
        }

        public MessageFormatter setString(String key, String value) {
            map.put(key, value);
            return this;
        }

        public Embed format(Embed embed) {
            return embed == null ? null : Embed.fromJson(format(embed.toJson()));
        }

        public String format(String str) {
            for (Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value == null) continue;
                str = str.replace(c + key + c, value);
            }
            return str;
        }
    }
}
