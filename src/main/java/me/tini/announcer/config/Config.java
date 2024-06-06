/*
 * Copyright (C) 2020  OopsieWoopsie
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

package me.tini.announcer.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.spicord.embed.EmbedLoader;

import eu.mcdb.universal.config.YamlConfiguration;
import lombok.Getter;
import me.tini.announcer.BanAnnouncerPlugin;

public class Config {

    private final static int CONFIG_VERSION = 4;

    @Getter private static Config instance;

    private File dataFolder;
    private EmbedLoader embedLoader;
    private File configFile;
    private Logger logger;

    @Getter private long channelToAnnounce;
    @Getter private Messages messages;

    @Getter private boolean ignoreSilent;
    @Getter private String consoleName;
    @Getter private String automaticText;

    @Getter private boolean useDiscordCommand;

    @Getter private boolean autoDetect;

    @Getter private Set<String> enabledExtensions;

    public Config(BanAnnouncerPlugin plugin) {
        instance = this;
        this.dataFolder = plugin.getDataFolder();
        this.logger = plugin.getLogger();

        try {
            this.embedLoader = EmbedLoader.extractAndLoad(plugin.getFile(), new File(dataFolder, "embed"));
        } catch (IOException e) {
            throw new RuntimeException("An error ocurred while extracting the embed files", e);
        }

        this.configFile = new File(dataFolder, "config.yml");
        this.loadConfig(false);
    }

    public void reload() {
        this.loadConfig(true);
    }

    public String getExpiredOperatorName() {
        return String.format("%s (%s)", consoleName, automaticText);
    }

    private void loadConfig(boolean reload) {
        try {
            if (!configFile.exists())
                createConfig();

            final YamlConfiguration config = YamlConfiguration.load(configFile);
            final int file_version = config.getInt("config-version", 0);

            if (file_version < CONFIG_VERSION && !reload) {
                final File oldConfig = new File(dataFolder, "config.yml." + file_version);

                if (oldConfig.exists())
                    oldConfig.delete();

                configFile.renameTo(oldConfig);

                logger.warning("An outdated config was found and it was renamed to '" + oldConfig.getName() + "'.");

                createConfig();

                loadConfig(false);
            } else {
                if (reload) {
                    messages.reload();
                } else {
                    messages = new Messages(embedLoader, config, dataFolder);
                }

                List<Long> oldConfigChannels = config.getLongList("channels-to-announce");

                channelToAnnounce = config.getLong(
                    "channel-to-announce",
                    oldConfigChannels != null && !oldConfigChannels.isEmpty() ? oldConfigChannels.get(0) : 0L
                );

                String punishmentManager = config.getString("punishment-manager", "auto");
                String jailManager       = config.getString("jail-manager", "off");

                Set<String> legacyEnabled = new HashSet<>();

                if (punishmentManager != null) {
                    if ("auto".equals(punishmentManager)) {
                        this.autoDetect = true;
                    } else {
                        legacyEnabled.add(punishmentManager);
                    }
                }
                if (jailManager != null) {
                    if (!"off".equals(jailManager) && !"false".equals(jailManager)) {
                        legacyEnabled.add(jailManager);
                    }
                }
                List<String> enabledExt = config.getStringList("enabled-extensions");
                if (enabledExt != null) {
                    legacyEnabled.addAll(enabledExt);
                }
                this.enabledExtensions = legacyEnabled;

                ignoreSilent      = config.getBoolean("ignore-silent", false);
                consoleName       = config.getString("console-name", "Console");
                automaticText     = config.getString("automatic", "Automatic");
                useDiscordCommand = config.getBoolean("enable-discord-command", true);
            }
        } catch (Exception e) {
            logger.severe("This is a configuration error, NOT a plugin error, please generate a new config or fix it.");
            e.printStackTrace();
        }
    }

    private void createConfig() throws IOException {
        if (!dataFolder.exists())
            dataFolder.mkdir();

        try (final InputStream in = getClass().getResourceAsStream("/config.yml")) {
            Files.copy(in, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
