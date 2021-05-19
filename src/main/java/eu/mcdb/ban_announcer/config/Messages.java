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
import eu.mcdb.spicord.embed.Embed;
import eu.mcdb.spicord.embed.EmbedLoader;
import eu.mcdb.universal.config.YamlConfiguration;
import lombok.Getter;

@Getter 
public final class Messages {

    private Embed kick;
    private Embed jail;

    private Embed ban;
    private Embed banip;
    private Embed mute;
    private Embed warn;
    private Embed note;

    private Embed tempban;
    private Embed tempbanip;
    private Embed tempmute;
    private Embed tempwarn;

    private Embed unjail;
    private Embed unban;
    private Embed unbanip;
    private Embed unwarn;
    private Embed unmute;
    private Embed unnote;

    private final EmbedLoader embedLoader;
    private final YamlConfiguration config;
    private final File embedFolder;

    protected Messages(EmbedLoader embedLoader, YamlConfiguration config, File dataFolder) {
        this.embedLoader = embedLoader;
        this.config = config;
        this.embedFolder = new File(dataFolder, "embed");
        this.load();
    }

    private void load() {
        this.kick      = getEmbed("kick");
        this.note      = getEmbed("note");
        this.jail      = getEmbed("jail");
        this.ban       = getEmbed("ban");
        this.tempban   = getEmbed("tempban");
        this.mute      = getEmbed("mute");
        this.tempmute  = getEmbed("tempmute");
        this.banip     = getEmbed("banip");
        this.tempbanip = getEmbed("tempbanip");
        this.warn      = getEmbed("warn");
        this.tempwarn  = getEmbed("tempwarn");
        this.unbanip   = getEmbed("unbanip");
        this.unwarn    = getEmbed("unwarn");
        this.unmute    = getEmbed("unmute");
        this.unban     = getEmbed("unban");
        this.unjail    = getEmbed("unjail");
        this.unnote    = getEmbed("unnote");
    }

    public void reload() {
        this.embedLoader.load(embedFolder);
        this.load();
    }

    private Embed getEmbed(final String key) {
        String message = config.getString("messages." + key);

        if (message != null) {
            message = message.trim();
        } else {
            message = "{embed:" + key + "}";
        }

        if (message.startsWith("{embed:") && message.endsWith("}")) {
            final String embedName = message.substring(7, message.length() - 1).trim();

            return embedLoader.getEmbedByName(embedName);
        } else if (!message.isEmpty()) {
            return Embed.fromString(message);
        }

        return null;
    }
}
