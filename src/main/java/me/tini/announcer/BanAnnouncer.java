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

package me.tini.announcer;

import static me.tini.announcer.PunishmentInfo.Type.*;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;

import org.spicord.Spicord;
import org.spicord.bot.DiscordBot;
import org.spicord.embed.Embed;
import org.spicord.embed.EmbedSender;

import lombok.Getter;
import me.tini.announcer.config.Config;
import me.tini.announcer.config.Messages;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.extension.ExtensionContainer;
import me.tini.announcer.extension.FileExtensionContainer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public final class BanAnnouncer {

    private Set<ExtensionContainer> allExtensions = new HashSet<>(0);

    @Getter private Config config;
    @Getter private Logger logger;
    @Getter private boolean enabled = true;

    private Map<PunishmentInfo.Type, Function<PunishmentInfo, Embed>> callbacks;
    private DiscordBot bot;

    private Map<String, Function<PunishmentInfo, String>> allPlaceholders = new HashMap<>();

    public BanAnnouncer(Config config, Spicord spicord) {
        this.config = config;
        this.logger = config.getLogger();

        this.callbacks = new EnumMap<>(PunishmentInfo.Type.class);

        BiFunction<PunishmentInfo, Embed, Embed> builder;

        allPlaceholders.put("id",          punishment -> punishment.getId());
        allPlaceholders.put("player",      punishment -> punishment.getPlayer());
        allPlaceholders.put("player_uuid", punishment -> punishment.getPlayerId());
        allPlaceholders.put("staff",       punishment -> punishment.getOperator());
        allPlaceholders.put("reason",      punishment -> punishment.getReason());
        allPlaceholders.put("duration",    punishment -> punishment.getDuration());
        allPlaceholders.put("jail",        punishment -> punishment.getJail());

        allPlaceholders.put("litebans_server_origin",   punishment -> punishment.getLitebansServerOrigin());
        allPlaceholders.put("litebans_server_scope",    punishment -> punishment.getLitebansServerScope());
        allPlaceholders.put("litebans_random_id",       punishment -> punishment.getLitebansRandomId());
        allPlaceholders.put("litebans_removal_reason",  punishment -> punishment.getLitebansRemovalReason());
        allPlaceholders.put("litebans_removed_by_name", punishment -> punishment.getLitebansRemovedByName());

        for (String fmt : new String[] { "t", "T", "d", "D", "f", "F", "R" }) {
            allPlaceholders.put("date_start_" + fmt, punishment -> {
                final long seconds;

                if (punishment.isPermanent()) {
                    seconds = System.currentTimeMillis() / 1000L;
                } else {
                    seconds = punishment.getDateStart() / 1000L;
                }

                return "<t:" + seconds + ":" + fmt + ">";
            });

            allPlaceholders.put("date_end_" + fmt, punishment -> {
                final long seconds;

                if (punishment.isPermanent()) {
                    seconds = dateToMillis("31 Dec 9999") / 1000L;
                } else {
                    seconds = punishment.getDateEnd() / 1000L;
                }

                return "<t:" + seconds + ":" + fmt + ">";
            });
        }

        builder = (punishment, template) -> {
            MessageFormatter mf = new MessageFormatter();

            mf.setOtherPlaceholderHandler(placeholder -> processPlaceholder(punishment, placeholder));

            for (Entry<String, Function<PunishmentInfo, String>> placeholders : allPlaceholders.entrySet()) {
                String placeholder = placeholders.getKey();
                String value = placeholders.getValue().apply(punishment);

                mf.setString(placeholder, value);
            }

            return mf.format(template);
        };

        Messages messages = config.getMessages();

        callbacks.put(BAN,     p -> builder.apply(p, p.isPermanent() ? messages.getBan() : messages.getTempban()));
        callbacks.put(BANIP,   p -> builder.apply(p, p.isPermanent() ? messages.getBanip() : messages.getTempbanip()));
        callbacks.put(MUTE,    p -> builder.apply(p, p.isPermanent() ? messages.getMute() : messages.getTempmute()));
        callbacks.put(WARN,    p -> builder.apply(p, p.isPermanent() ? messages.getWarn() : messages.getTempwarn()));
        callbacks.put(KICK,    p -> builder.apply(p, messages.getKick()));
        callbacks.put(NOTE,    p -> builder.apply(p, messages.getNote()));
        callbacks.put(UNNOTE,  p -> builder.apply(p, messages.getUnnote()));
        callbacks.put(JAIL,    p -> builder.apply(p, messages.getJail()));
        callbacks.put(UNJAIL,  p -> builder.apply(p, messages.getUnjail()));
        callbacks.put(UNBAN,   p -> builder.apply(p, messages.getUnban()));
        callbacks.put(UNBANIP, p -> builder.apply(p, messages.getUnbanip()));
        callbacks.put(UNMUTE,  p -> builder.apply(p, messages.getUnmute()));
        callbacks.put(UNWARN,  p -> builder.apply(p, messages.getUnwarn()));

        callbacks.put(TEMPBAN,   callbacks.get(BAN));
        callbacks.put(TEMPBANIP, callbacks.get(BANIP));
        callbacks.put(TEMPMUTE,  callbacks.get(MUTE));
        callbacks.put(TEMPWARN,  callbacks.get(WARN));
    }

    public void handlePunishment(PunishmentInfo punishment, PunishmentListener listener) {
        if (!enabled) {
            logger.warning("BanAnnouncer is not enabled, ignoring punishment.");
            logger.warning(punishment.toString());
            return;
        }

        logger.info("Got announcement request from the '" + listener.getName() + "' listener.");

        Embed embed = buildEmbed(punishment);
        sendDiscordMessage(embed);
    }

    public Embed buildEmbed(PunishmentInfo punishment) {
        return callbacks.get(punishment.getType()).apply(punishment);
    }

    private void sendDiscordMessage(Embed message) {
        if (message == null) {
            logger.warning("(message is null, ignoring it)");
            return;
        }

        if (bot == null || bot.getJda() == null) {
            logger.warning("BanAnnouncer does not have access to an active bot.");
            return;
        }

        JDA jda = bot.getJda();

        long channelId = config.getChannelToAnnounce();

        GuildChannel channel = jda.getGuildChannelById(channelId);

        if (channel == null || !(channel instanceof GuildMessageChannel)) {
            logger.severe("Cannot find the channel with id '" + channelId + "'. The message was not sent.");
            return;
        } else {
            EmbedSender.prepare((GuildMessageChannel) channel, message).queue(success -> {
                logger.info("The punishment message was sent.");
            }, fail -> {
                logger.warning("Couldn't send the punishment message: " + fail.getMessage());
            });
        }
    }

    public void setBot(DiscordBot bot) {
        this.bot = bot;
    }

    public void removeBot(DiscordBot bot) {
        this.bot = null;
    }

    public void disable() {
        for (ExtensionContainer ext : allExtensions) {
            ext.close();
        }
        allExtensions.clear();
        callbacks.clear();
        bot = null;
        enabled = false;
        config = null;
        callbacks = null;
    }

    public Set<ExtensionContainer> loadExtensions(File folder) {
        if (folder.mkdirs()) {
            return Collections.emptySet();
        }

        File[] files = folder.listFiles((d, name) -> name.endsWith(".jar") || name.endsWith(".ext"));

        Set<ExtensionContainer> extensions = new HashSet<>(files.length, 1.0f);

        for (File file : files) {
            ExtensionContainer loader = new FileExtensionContainer(file);

            extensions.add(loader);

            logger.info("Loaded '" + loader.getInfo().getName() + "' extension.");
        }

        allExtensions.addAll(extensions);

        return extensions;
    }

    public Set<ExtensionContainer> getExtensions() {
        return allExtensions;
    }

    public void registerPlaceholder(String placeholder, Function<PunishmentInfo, String> provider) {
        allPlaceholders.put(placeholder, provider);
    }

    public String processPlaceholder(PunishmentInfo info, String placeholder) {
        for (ExtensionContainer loader : allExtensions) {
            if (loader.isInstanceCreated()) {
                AbstractExtension extension = loader.getInstance();
                String result = extension.processPlaceholder(info, placeholder);

                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private long dateToMillis(String date) {
        return new Date(date).getTime();
    }
}
