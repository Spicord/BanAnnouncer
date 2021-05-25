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

package eu.mcdb.ban_announcer.listener;

import eu.mcdb.ban_announcer.PunishmentAction;
import eu.mcdb.ban_announcer.PunishmentAction.Type;
import eu.mcdb.ban_announcer.BanAnnouncer;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public final class AdvancedBan {

    public static void onPunishmentAction(final Punishment pun, final boolean revoked) {
        final BanAnnouncer bann = BanAnnouncer.getInstance();
        final PunishmentAction punishment = new PunishmentAction();

        String operator = "Console".equalsIgnoreCase(pun.getOperator())
                ? bann.getConfig().getConsoleName()
                : pun.getOperator();

        punishment.setId(String.valueOf(pun.getId()));
        punishment.setOperator(operator);
        punishment.setPlayer(pun.getName());

        if (revoked) {
            if (pun.isExpired()) { // automatic
                punishment.setOperator(bann.getConfig().getExpiredOperatorName());
            }
            if (pun.getType() == PunishmentType.NOTE) {
                punishment.setReason(pun.getReason());
            }
        } else {
            punishment.setReason(pun.getReason());
            punishment.setDuration(pun.getDuration(true));
            punishment.setPermanent(punishment.getDuration().equals("permanent"));
        }

        switch (pun.getType()) {
        case KICK:
            punishment.setType(Type.KICK);
            break;
        case BAN:
            punishment.setType(revoked ? Type.UNBAN : Type.BAN);
            break;
        case TEMP_BAN:
            punishment.setType(revoked ? Type.UNBAN : Type.TEMPBAN);
            break;
        case IP_BAN:
            punishment.setType(revoked ? Type.UNBANIP : Type.BANIP);
            break;
        case TEMP_IP_BAN:
            punishment.setType(revoked ? Type.UNBANIP : Type.TEMPBANIP);
            break;
        case MUTE:
            punishment.setType(revoked ? Type.UNMUTE : Type.MUTE);
            break;
        case TEMP_MUTE:
            punishment.setType(revoked ? Type.UNMUTE : Type.TEMPMUTE);
            break;
        case WARNING:
            punishment.setType(revoked ? Type.UNWARN : Type.WARN);
            break;
        case TEMP_WARNING:
            punishment.setType(revoked ? Type.UNWARN : Type.TEMPWARN);
            break;
        case NOTE:
            punishment.setType(revoked ? Type.UNNOTE : Type.NOTE);
            break;
        default:
            bann.getLogger().severe("Unknown punishment type '" + pun.getType() + "'.");
            return;
        }

        bann.handlePunishmentAction(punishment);
    }
}
