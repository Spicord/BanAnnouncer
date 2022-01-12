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

import java.io.File;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
import eu.mcdb.ban_announcer.config.Config;
import eu.mcdb.ban_announcer.config.Messages;
import eu.mcdb.ban_announcer.extension.Extension;
import eu.mcdb.ban_announcer.extension.ExtensionClassLoader;

import org.spicord.Spicord;
import org.spicord.bot.DiscordBot;
import org.spicord.embed.Embed;
import org.spicord.embed.EmbedSender;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

public final class BanAnnouncer {

    private Set<Extension> allExtensions = new HashSet<Extension>(0);

    @Getter private Config config;
    @Getter private Logger logger;
    @Getter private boolean enabled = true;

    private Map<PunishmentAction.Type, Function<PunishmentAction, Embed>> callbacks;
    private DiscordBot bot;

    public BanAnnouncer(Config config, Spicord spicord) {
        this.config = config;
        this.logger = config.getLogger();

        this.callbacks = new EnumMap<>(PunishmentAction.Type.class);

        BiFunction<PunishmentAction, Embed, Embed> builder;

        builder = (punishment, template) -> new MessageFormatter()
                .setString("id",       punishment.getId())
                .setString("player",   punishment.getPlayer())
                .setString("staff",    punishment.getOperator())
                .setString("reason",   punishment.getReason())
                .setString("duration", punishment.getDuration())
                .setString("jail",     punishment.getJail())
                .format(template);

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

        JDA jda = bot.getJda();

        long channelId = config.getChannelsToAnnounce().get(0);

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
    }

    public void setBot(DiscordBot bot) {
        this.bot = bot;
    }

    public void removeBot(DiscordBot bot) {
        this.bot = null;
    }

    public void disable() {
        for (Extension ext : allExtensions) {
            ext.unload();
        }
        allExtensions.clear();
        callbacks.clear();
        bot = null;
        enabled = false;
        config = null;
        callbacks = null;
    }

    public Set<Extension> loadExtensions(File folder) {
        if (folder.mkdirs()) {
            return Collections.emptySet();
        }

        File[] files = folder.listFiles((d, name) -> name.endsWith(".jar")||name.endsWith(".ext"));

        Set<Extension> extensions = new HashSet<>(files.length, 1.0f);

        for (File file : files) {
            @SuppressWarnings("resource")
            ExtensionClassLoader loader = new ExtensionClassLoader(file);

            Extension ext = loader.getExtension();

            if (ext == null) {
                loader.close();
                continue; // TODO: warning?
            }

            extensions.add(ext);

            logger.info("Loaded " + ext.getName() + " extension.");
        }

        allExtensions.addAll(extensions);

        return extensions;
    }

    public Set<Extension> getExtensions() {
        return allExtensions;
    }
}
