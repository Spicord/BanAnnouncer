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

package eu.mcdb.ban_announcer.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import eu.mcdb.spicord.embed.EmbedLoader;
import eu.mcdb.universal.config.YamlConfiguration;
import lombok.Getter;

public class Config {

    private final static int CONFIG_VERSION = 4;

    @Getter private EmbedLoader embedLoader;
    private File dataFolder;
    private File configFile;
    private Logger logger;

    public static List<Long> CHANNELS_TO_ANNOUNCE = new ArrayList<Long>();
    public static Messages MESSAGES;
    @Getter private String punishmentManager;
    @Getter private boolean ignoreSilent;

    @Getter private static Config instance;

    public Config(File zip, File dataFolder) {
        instance = this;
        this.dataFolder = dataFolder;

        try {
            this.embedLoader = EmbedLoader.extractAndLoad(zip, new File(dataFolder, "embed"));
        } catch (IOException e) {
            throw new RuntimeException("An error ocurred while extracting the embed files", e);
        }

        this.configFile = new File(dataFolder, "config.yml");
        this.loadConfig();
    }

    private void loadConfig() {
        try {
            if (!configFile.exists())
                createConfig();

            final YamlConfiguration config = YamlConfiguration.load(configFile);
            final int file_version = config.getInt("config-version", 0);

            if (file_version < CONFIG_VERSION) {
                final File oldConfig = new File(dataFolder, "config.yml." + file_version);

                if (oldConfig.exists())
                    oldConfig.delete();

                configFile.renameTo(oldConfig);

                logger.warning("An outdated config was found and it was renamed to '" + oldConfig.getName() + "'.");

                createConfig();

                loadConfig();
            } else {
                MESSAGES = new Messages(embedLoader, config);
                CHANNELS_TO_ANNOUNCE = config.getLongList("channels-to-announce");
                punishmentManager = config.getString("punishment-manager", "auto");
                ignoreSilent = config.getBoolean("ignore-silent", false);
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
        } catch (IOException e) {
            throw e;
        }
    }
}
