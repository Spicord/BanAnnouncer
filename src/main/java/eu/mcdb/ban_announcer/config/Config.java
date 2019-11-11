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

package eu.mcdb.ban_announcer.config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.bukkit.configuration.file.FileConfiguration;
import eu.mcdb.ban_announcer.bukkit.BanAnnouncerBukkit;
import eu.mcdb.ban_announcer.bungee.BanAnnouncerBungee;
import eu.mcdb.spicord.embed.EmbedLoader;
import eu.mcdb.util.Server;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public final class Config {

    public static List<Long> CHANNELS_TO_ANNOUNCE = new ArrayList<Long>();
    public static Messages MESSAGES;
    private int config_version = 3;
	private File file;
    private File dataFolder;
	private Object pl;

	@Getter
    private String punishmentManager;

	@Getter
    private EmbedLoader embedLoader;

	@Getter
    private boolean ignoreSilent;

	@Getter
    private static Config instance;

    public Config(Object pl) {
    	this.pl = pl;
        instance = this;
        switch (Server.getServerType()) {
        case BUKKIT:
            loadBukkit();
            break;
        case BUNGEECORD:
            loadBungee();
            break;
        }
    }

    private void loadBungee() {
        BanAnnouncerBungee plugin = (BanAnnouncerBungee) pl;
        plugin.getDataFolder().mkdir();
        try {
            this.file = plugin.getFile();
            dataFolder = plugin.getDataFolder();
            extractEmbeds();
            this.embedLoader = new EmbedLoader();
            embedLoader.load(new File(plugin.getDataFolder(), "embed"));

            File configFile = createIfNotExists(plugin.getDataFolder());
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            int cfg_ver = config.getInt("config-version", 0);

            if (cfg_ver != config_version) {
                File newFile = new File(plugin.getDataFolder(), "config.yml." + cfg_ver);

                if (newFile.exists())
                    newFile.delete();

                configFile.renameTo(newFile);
                plugin.getLogger()
                        .warning("An outdated config was found and it was renamed to '" + newFile.getName() + "'");
                loadBungee();
            } else {
                MESSAGES = new Messages(this, config, config.getClass().getDeclaredMethod("getString", String.class));
                CHANNELS_TO_ANNOUNCE = config.getLongList("channels-to-announce");
                punishmentManager = config.getString("punishment-manager", "auto");
                ignoreSilent = config.getBoolean("ignore-silent", false);
            }
        } catch (Exception e) {
            plugin.getLogger().severe(
                    "This is a configuration error, NOT a plugin error, please generate a new config or fix it. "
                            + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadBukkit() {
        BanAnnouncerBukkit plugin = (BanAnnouncerBukkit) pl;
        plugin.getDataFolder().mkdir();

        try {
            this.file = plugin.getFile();
            this.dataFolder = plugin.getDataFolder();
            extractEmbeds();
            this.embedLoader = new EmbedLoader();
            embedLoader.load(new File(plugin.getDataFolder(), "embed"));

            File configFile = createIfNotExists(plugin.getDataFolder());
            FileConfiguration config = plugin.getConfig();

            int cfg_ver = config.getInt("config-version", 0);

            if (cfg_ver != config_version) {
                File newFile = new File(plugin.getDataFolder(), "config.yml." + cfg_ver);

                if (newFile.exists())
                    newFile.delete();

                configFile.renameTo(newFile);
                plugin.getLogger()
                        .warning("An outdated config was found and it was renamed to '" + newFile.getName() + "'.");
                loadBukkit();
            } else {
                MESSAGES = new Messages(this, config, config.getClass().getSuperclass().getSuperclass().getSuperclass()
                        .getDeclaredMethod("getString", String.class));
                CHANNELS_TO_ANNOUNCE = config.getLongList("channels-to-announce");
                punishmentManager = config.getString("punishment-manager", "auto");
                ignoreSilent = config.getBoolean("ignore-silent", false);
            }
        } catch (Exception e) {
            plugin.getLogger().severe(
                    "This is a configuration error, NOT a plugin error, please generate a new config or fix it. "
                            + e.getMessage());
            e.printStackTrace();
        }
    }

    private File createIfNotExists(File dataFolder) {
        if (!dataFolder.exists())
            dataFolder.mkdir();

        File file = new File(dataFolder, "config.yml");

        if (!file.exists()) {
            try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                Files.copy(in, file.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    private void extractEmbeds() {
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();

            File embedsFolder = new File(dataFolder, "embed");

            if (!embedsFolder.exists())
                embedsFolder.mkdir();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.getName().startsWith("embed/") && entry.getName().endsWith(".json")) {
                    String embedName = entry.getName();
                    embedName = embedName.substring(embedName.lastIndexOf("/") + 1);

                    File file = new File(embedsFolder, embedName);

                    if (!file.exists()) {
                        Files.copy(jarFile.getInputStream(entry), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            jarFile.close();
        } catch (Exception e) {
        }
    }
}
