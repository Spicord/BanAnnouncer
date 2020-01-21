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

import eu.mcdb.spicord.embed.Embed;
import eu.mcdb.spicord.embed.EmbedLoader;
import eu.mcdb.universal.config.YamlConfiguration;

public final class Messages {

    public final Embed KICK;
    public final Embed BAN;
    public final Embed TEMPBAN;
    public final Embed MUTE;
    public final Embed TEMPMUTE;
    public final Embed BANIP;
    public final Embed TEMPBANIP;
    public final Embed WARN;
    public final Embed TEMPWARN;

    public final Embed UNBANIP;
    public final Embed UNWARN;
    public final Embed UNMUTE;
    public final Embed UNBAN;

    private final EmbedLoader embedLoader;
    private final YamlConfiguration config;

    protected Messages(EmbedLoader embedLoader, YamlConfiguration config) {
        this.embedLoader = embedLoader;
        this.config = config;

        this.KICK      = getEmbed("kick");
        this.BAN       = getEmbed("ban");
        this.TEMPBAN   = getEmbed("tempban");
        this.MUTE      = getEmbed("mute");
        this.TEMPMUTE  = getEmbed("tempmute");
        this.BANIP     = getEmbed("banip");
        this.TEMPBANIP = getEmbed("tempbanip");
        this.WARN      = getEmbed("warn");
        this.TEMPWARN  = getEmbed("tempwarn");
        this.UNBANIP   = getEmbed("unbanip");
        this.UNWARN    = getEmbed("unwarn");
        this.UNMUTE    = getEmbed("unmute");
        this.UNBAN     = getEmbed("unban");
    }

    private Embed getEmbed(final String key) {
        final String message = config.getString("messages." + key).trim();

        if (message.startsWith("{embed:") && message.endsWith("}")) {
            final String embedName = message.substring(7, message.length() - 1).trim();

            return embedLoader.getEmbedByName(embedName);
        } else if (!message.isEmpty()) {
            return Embed.fromString(message);
        }

        return null;
    }
}
