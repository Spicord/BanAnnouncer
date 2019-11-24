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

import java.lang.reflect.Method;
import eu.mcdb.spicord.embed.Embed;

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

    public final Embed UNPUNISH;

    private final Object configInstance;
    private final Method getStringMethod;
    private Config config;

    protected Messages(Config config, Object configInstance, Method getStringMethod) {
        this.config = config;
        this.configInstance = configInstance;
        this.getStringMethod = getStringMethod;

        this.KICK      = getEmbed("kick");
        this.BAN       = getEmbed("ban");
        this.TEMPBAN   = getEmbed("tempban");
        this.MUTE      = getEmbed("mute");
        this.TEMPMUTE  = getEmbed("tempmute");
        this.BANIP     = getEmbed("banip");
        this.TEMPBANIP = getEmbed("tempbanip");
        this.WARN      = getEmbed("warn");
        this.TEMPWARN  = getEmbed("tempwarn");
        this.UNPUNISH = getEmbed("unpunish");
    }

    private Embed getEmbed(String key) {
        try {
            String message = ((String) getStringMethod.invoke(configInstance, "messages." + key)).trim();
            if (message.startsWith("{embed:") && message.endsWith("}")) {
                String embedName = message.substring(7, message.length() - 1).trim();
                return config.getEmbedLoader().getEmbedByName(embedName);
            } else if (!message.isEmpty()) {
                return Embed.fromString(message);
            }
        } catch (Exception e) {
            System.out.println("err: " + key);
            e.printStackTrace();
        }
        return null;
    }
}
