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

package eu.mcdb.ban_announcer.listener;

import eu.mcdb.ban_announcer.BAPunishment;
import eu.mcdb.ban_announcer.BAPunishment.Type;
import eu.mcdb.ban_announcer.BanAnnouncer;
import me.leoko.advancedban.utils.Punishment;

public final class AdvancedBan {

    public static void onPunishment(Punishment abp) {
    	BanAnnouncer ba = BanAnnouncer.getInstance();

    	BAPunishment punishment = new BAPunishment();

        punishment.setDuration(abp.getDuration(true));
        punishment.setOperator(abp.getOperator());
        punishment.setReason(abp.getReason());
        punishment.setPlayer(abp.getName());
        punishment.setPermanent(punishment.getDuration().equals("permanent"));

        switch (abp.getType()) {
        case KICK:
            punishment.setType(Type.KICK);
            break;
        case BAN:
            punishment.setType(Type.BAN);
            break;
        case TEMP_BAN:
            punishment.setType(Type.TEMPBAN);
            break;
        case IP_BAN:
            punishment.setType(Type.BANIP);
            break;
        case TEMP_IP_BAN:
            punishment.setType(Type.TEMPBANIP);
            break;
        case MUTE:
            punishment.setType(Type.MUTE);
            break;
        case TEMP_MUTE:
            punishment.setType(Type.TEMPMUTE);
            break;
        case WARNING:
            punishment.setType(Type.WARN);
            break;
        case TEMP_WARNING:
            punishment.setType(Type.TEMPWARN);
            break;
        default:
            ba.getLogger().severe("Unknown event '" + abp.getType() + "'.");
            return;
        }

        ba.handlePunishment(punishment);
    }
}
