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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import eu.mcdb.ban_announcer.addon.BanAnnouncerAddon;
import eu.mcdb.ban_announcer.config.Config;
import eu.mcdb.spicord.Spicord;
import eu.mcdb.spicord.bot.DiscordBot;
import eu.mcdb.spicord.embed.Embed;
import eu.mcdb.spicord.embed.EmbedSender;
import eu.mcdb.util.chat.ChatColor;
import lombok.Getter;
import net.dv8tion.jda.core.entities.TextChannel;

public final class BanAnnouncer {

	@Getter
    private static BanAnnouncer instance;

	@Getter
    private final Logger logger;

	private final Set<DiscordBot> bots;
    private boolean enabled = true;

    public BanAnnouncer(Logger logger) {
        instance = this;
        this.logger = logger;
        this.bots = Collections.synchronizedSet(new HashSet<DiscordBot>());
        Spicord.getInstance().getAddonManager().registerAddon(new BanAnnouncerAddon(this));
    }

    public void handlePunishment(BAPunishment punishment) {
        if (!enabled) {
            System.out.println("BanAnnouncer is not enabled, ignoring punishment.");
            System.out.println(punishment.toString());
            return;
        }

        String player = punishment.getPlayer();
        String operator = punishment.getOperator();
        String reason = ChatColor.stripColor(punishment.getReason());
        reason = reason.equals("") ? "none" : reason;
        String duration = ChatColor.stripColor(punishment.getDuration());
        boolean permanent = punishment.isPermanent();

        switch (punishment.getType()) {
        case BAN:
        case TEMPBAN:
            sendBanMessage(player, operator, reason, duration, permanent);
            break;
        case KICK:
            sendKickMessage(player, operator, reason);
            break;
        case MUTE:
        case TEMPMUTE:
            sendMuteMessage(player, operator, reason, duration, permanent);
            break;
        case WARN:
        case TEMPWARN:
            sendWarnMessage(player, operator, reason, duration, permanent);
            break;
        case BANIP:
        case TEMPBANIP:
            sendIpBanMessage(player, operator, reason, duration, permanent);
            break;
        case UNKNOWN:
        default:
        }
    }

    private void sendKickMessage(String player, String operator, String reason) {
        MessageFormatter formatter = new MessageFormatter()
                .setString("player", player)
                .setString("staff", operator)
                .setString("reason", reason);
        Embed embed = formatter.format(Config.MESSAGES.KICK);

        sendDiscordMessage(embed);
    }

    private void sendIpBanMessage(String player, String operator, String reason, String duration, boolean permanent) {
        MessageFormatter formatter = new MessageFormatter()
                .setString("player", player)
                .setString("staff", operator)
                .setString("reason", reason)
                .setString("duration", duration);
        Embed embed = formatter.format(permanent ? Config.MESSAGES.BANIP : Config.MESSAGES.TEMPBANIP);

        sendDiscordMessage(embed);
    }

    private void sendWarnMessage(String player, String operator, String reason, String duration, boolean permanent) {
        MessageFormatter formatter = new MessageFormatter()
                .setString("player", player)
                .setString("staff", operator)
                .setString("reason", reason)
                .setString("duration", duration);
        Embed embed = formatter.format(permanent ? Config.MESSAGES.WARN : Config.MESSAGES.TEMPWARN);

        sendDiscordMessage(embed);
    }

    private void sendBanMessage(String player, String operator, String reason, String duration, boolean permanent) {
        MessageFormatter formatter = new MessageFormatter()
                .setString("player", player)
                .setString("staff", operator)
                .setString("reason", reason)
                .setString("duration", duration);
        Embed embed = formatter.format(permanent ? Config.MESSAGES.BAN : Config.MESSAGES.TEMPBAN);

        sendDiscordMessage(embed);
    }

    private void sendMuteMessage(String player, String operator, String reason, String duration, boolean permanent) {
        MessageFormatter formatter = new MessageFormatter()
                .setString("player", player)
                .setString("staff", operator)
                .setString("reason", reason)
                .setString("duration", duration);
        Embed embed = formatter.format(permanent ? Config.MESSAGES.MUTE : Config.MESSAGES.TEMPMUTE);

        sendDiscordMessage(embed);
    }

    private void sendDiscordMessage(Embed message) {
        if (message == null) {
            System.out.println("Message is null, ignoring it.");
            return;
        }

        bots.stream().filter(DiscordBot::isReady).map(DiscordBot::getJda).forEach(jda -> {
            Config.CHANNELS_TO_ANNOUNCE.forEach(channelId -> {
                TextChannel channel = jda.getTextChannelById(channelId);

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
        enabled = false;
        instance = null;
        bots.clear();
    }

    private class MessageFormatter {

        private final Map<String, String> map;

        public MessageFormatter() {
            this.map = new HashMap<String, String>();
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
                str = str.replace("%" + entry.getKey() + "%", entry.getValue());
            }
            return str;
        }
    }
}
