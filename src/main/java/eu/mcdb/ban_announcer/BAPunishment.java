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

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public final class BAPunishment {

    private String player = "";
    private String operator = "";
    private String reason = "";
    private String duration = "";
    private boolean permanent = false;
    private Type type = Type.UNKNOWN;

    public BAPunishment() {}

    public BAPunishment(Type type) {
        this.type = type;
    }

    public enum Type {
        KICK, BAN, TEMPBAN, MUTE, TEMPMUTE, BANIP, TEMPBANIP, WARN, TEMPWARN, UNKNOWN;
    }
}