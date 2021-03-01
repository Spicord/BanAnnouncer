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
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.text.SimpleDateFormat;
import java.util.Date;

@ToString
@Data
public final class PunishmentAction {

    private String player = "";
    private String operator = "";
    private String reason = "";
    private String duration = "";
    private String time = "";
    private String date = "";
    private boolean permanent = false;
    private Type type = Type.UNKNOWN;

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

    Date now = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");

    public void setTime() {
        this.time = timeFormat.format(now);
    }

    public void setDate() {
        this.date = dateFormat.format(now);
    }

    public boolean isRevoked() {
        switch (type) {
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
        KICK, BAN, TEMPBAN, MUTE, TEMPMUTE, BANIP, TEMPBANIP, WARN, TEMPWARN, UNBAN, UNMUTE, UNBANIP, UNWARN, UNKNOWN;
    }
}
