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

import eu.mcdb.util.chat.ChatColor;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public final class PunishmentAction {

    private String id;
    private String player;
    private String operator;
    private String reason;
    private String duration;
    private String jail;
    private boolean permanent = false;
    private Type type;

    public PunishmentAction() {}

    public PunishmentAction(Type type) {
        this.type = type;
    }

    public void setReason(String reason) {
        reason = ChatColor.stripColor(reason);
        this.reason = reason.equals("") ? "none" : reason;
    }

    public void setDuration(String duration) {
        duration = ChatColor.stripColor(duration);
        this.duration = duration.equals("") ? "unknown" : duration;
    }

    public boolean isRevoked() {
        if (type == null) {
            throw new IllegalStateException("Type is not set");
        }
        switch (type) {
        case UNJAIL:
        case UNBAN:
        case UNBANIP:
        case UNMUTE:
        case UNWARN:
            return true;
        default:
            return false;
        }
    }

    public enum Type {
        KICK,
        JAIL, UNJAIL,
        BAN, TEMPBAN, UNBAN,
        MUTE, TEMPMUTE, UNMUTE,
        BANIP, TEMPBANIP, UNBANIP,
        WARN, TEMPWARN, UNWARN,
        NOTE, UNNOTE // AdvancedBan only
        ;
    }
}
