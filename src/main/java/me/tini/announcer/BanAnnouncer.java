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
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import lombok.Getter;
import me.tini.announcer.config.Config;
import me.tini.announcer.config.Messages;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.extension.ExtensionContainer;
import me.tini.announcer.extension.ExtensionInfo;
import me.tini.announcer.extension.FileExtensionContainer;
import me.tini.announcer.utils.Embed;

public abstract class BanAnnouncer {

    private Map<String, ExtensionContainer> allExtensions = new HashMap<>(0);

    @Getter protected Config config;
    @Getter protected final BanAnnouncerPlugin plugin;
    @Getter protected Logger logger;
    @Getter protected boolean enabled = false;

    private Map<PunishmentInfo.Type, Function<PunishmentInfo, Embed>> callbacks;

    private Map<String, Function<PunishmentInfo, String>> allPlaceholders = new HashMap<>();

    public BanAnnouncer(Config config, BanAnnouncerPlugin plugin) {
        this.config = config;
        this.plugin = plugin;
        this.logger = plugin.getLogger();

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

    public abstract void initialize();

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

    public abstract void sendDiscordMessage(Embed message);

    public void disable() {
        for (ExtensionContainer ext : allExtensions.values()) {
            ext.close();
        }
        allExtensions.clear();
        callbacks.clear();
        enabled = false;
        config = null;
        callbacks = null;
    }

    public void loadExtensions(File folder) {
        if (folder.mkdirs()) {
            return;
        }

        File[] files = folder.listFiles((d, name) -> name.endsWith(".jar") || name.endsWith(".ext"));

        for (File file : files) {
            ExtensionContainer container = new FileExtensionContainer(file);

            ExtensionInfo info = container.getInfo();
            plugin.log("[Extension] Loaded %s (id: %s)", info.getName(), info.getId());

            allExtensions.put(info.getId(), container);
        }
    }

    public Collection<ExtensionContainer> getExtensions() {
        return allExtensions.values();
    }

    public void registerPlaceholder(String placeholder, Function<PunishmentInfo, String> provider) {
        allPlaceholders.put(placeholder, provider);
    }

    public String processPlaceholder(PunishmentInfo info, String placeholder) {
        for (ExtensionContainer loader : allExtensions.values()) {
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

    public void registerExtension(String name, String id, Supplier<AbstractExtension> instanceSupplier, String requiredClass) {
        ExtensionContainer container = new ExtensionContainer(new ExtensionInfo(name, id, null, requiredClass), instanceSupplier);
        allExtensions.put(id, container);
        plugin.log("[Extension] Loaded %s (id: %s)", name, id);
    }

    public void enableExtensions() {
        int enabledCount = 0;

        for (String id : config.getEnabledExtensions()) {
            if (id == null || "null".equals(id)) continue;

            ExtensionContainer container = allExtensions.get(id);

            if (container == null) {
                plugin.warn("The extension with the id '%s' was not found", id);
                continue;
            }

            ExtensionInfo info = container.getInfo();

            if (!isClassPresent(info.getRequiredClass())) {
                plugin.warn("[Extension] %s (id: %s) will not work on this server because it is missing a dependency", info.getName(), info.getId());
                continue;
            }

            AbstractExtension instance = container.getInstanceSupplier(plugin).get();

            plugin.log("[Extension] Enabled %s (id: %s)", info.getName(), info.getId());

            PunishmentListener listener = instance.getPunishmentListener();
            if (listener != null) {
                listener.register();
            }

            enabledCount++;
        }

        if (config.isAutoDetect()) {
            for (ExtensionContainer ext : allExtensions.values()) {
                if (config.getEnabledExtensions().contains(ext.getName())) {
                    continue;
                }
                if (isClassPresent(ext.getInfo().getRequiredClass())) {
                    AbstractExtension instance;
                    try {
                        instance = ext.getInstanceSupplier(plugin).get();
                    } catch (Throwable e) {
                        plugin.log("[Extension] [AutoDetect] Failed to enable %s (id: %s)", ext.getInfo().getName(), ext.getInfo().getId());
                        e.printStackTrace();
                        continue;
                    }

                    plugin.log("[Extension] [AutoDetect] Enabled %s (id: %s)", ext.getInfo().getName(), ext.getInfo().getId());

                    PunishmentListener listener = instance.getPunishmentListener();
                    if (listener != null) {
                        listener.register();
                    }

                    enabledCount++;
                }
            }
        }

        if (enabledCount == 0) {
            plugin.warn("None of the extensions were enabled!");
        }
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, BanAnnouncer.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {}
        return false;
    }

    public static BanAnnouncer build(BanAnnouncerPlugin plugin, Config config) {
        String mode = config.getMode();

        if ("spicord".equals(mode)) {
            return new BanAnnouncerSpicord(config, plugin);
        }
        if ("webhook".equals(mode)) {
            return new BanAnnouncerWebhook(config, plugin);
        }

        throw new IllegalArgumentException("Invalid mode: " + mode);
    }
}
