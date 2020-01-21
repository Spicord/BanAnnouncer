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
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import eu.mcdb.ban_announcer.bukkit.BanAnnouncerBukkit;
import eu.mcdb.ban_announcer.bungee.BanAnnouncerBungee;
import eu.mcdb.spicord.embed.EmbedLoader;
import eu.mcdb.universal.config.YamlConfiguration;
import eu.mcdb.util.Server;
import lombok.Getter;

public class Config {

    private final static int CONFIG_VERSION = 4;

    public static List<Long> CHANNELS_TO_ANNOUNCE = new ArrayList<Long>();
    public static Messages MESSAGES;

    private File file;
    private File dataFolder;
    private Object plugin;

    @Getter
    private String punishmentManager;

    @Getter
    private EmbedLoader embedLoader;

    @Getter
    private boolean ignoreSilent;

    private File configFile;
    private Logger logger;

    @Getter
    private static Config instance;

    public Config(Object plugin) {
        this.plugin = plugin;
        instance = this;
        switch (Server.getServerType()) {
        case BUKKIT:
            loadBukkit();
            break;
        case BUNGEECORD:
            loadBungee();
            break;
        default:
            throw new RuntimeException("");
        }

        this.extractEmbeds();
        this.embedLoader = new EmbedLoader();
        this.embedLoader.load(new File(dataFolder, "embed"));

        this.configFile = new File(dataFolder, "config.yml");
        this.loadConfig();
    }

    private void loadBungee() {
        final BanAnnouncerBungee plugin = (BanAnnouncerBungee) this.plugin;
        this.dataFolder = plugin.getDataFolder();
        this.file = plugin.getFile();
        this.logger = plugin.getLogger();
    }

    private void loadBukkit() {
        final BanAnnouncerBukkit plugin = (BanAnnouncerBukkit) this.plugin;
        this.dataFolder = plugin.getDataFolder();
        this.file = plugin.getFile();
        this.logger = plugin.getLogger();
    }

    private void loadConfig() {
        try {
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

    private void extractEmbeds() {
        try (final JarFile jarFile = new JarFile(file)) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            final File embedsFolder = new File(dataFolder, "embed");

            if (!embedsFolder.exists())
                embedsFolder.mkdirs();

            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();

                if (entry.getName().startsWith("embed/") && entry.getName().endsWith(".json")) {
                    String embedName = entry.getName();
                    embedName = embedName.substring(embedName.lastIndexOf("/") + 1);

                    final File file = new File(embedsFolder, embedName);

                    if (!file.exists()) {
                        Files.copy(jarFile.getInputStream(entry), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
