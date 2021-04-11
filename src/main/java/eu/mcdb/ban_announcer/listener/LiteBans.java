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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import eu.mcdb.ban_announcer.PunishmentAction;
import eu.mcdb.ban_announcer.PunishmentAction.Type;
import eu.mcdb.ban_announcer.BanAnnouncer;
import litebans.api.Database;
import litebans.api.Entry;
import litebans.api.Events;
import litebans.api.Events.Listener;

public final class LiteBans {

    private final Events events;
    private final Database database;
    private final BanAnnouncer bann;

    public LiteBans(final BanAnnouncer bann) {
        this.bann = bann;
        this.events = Events.get();
        this.database = Database.get();

        events.register(new LiteBansListener());
    }

    private String getName(final String uuid) {
        final String sentence = "SELECT name FROM {history} WHERE uuid = ? ORDER BY id DESC LIMIT 1";

        try (final PreparedStatement stmt = database.prepareStatement(sentence)) {
            stmt.setString(1, uuid);

            final ResultSet rs = stmt.executeQuery();

            if (rs.first())
                return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class LiteBansListener extends Listener {

        @Override
        public void entryAdded(final Entry entry) {
            if (!entry.isActive() && !entry.getType().equals("kick"))
                return;

            if (entry.isSilent() && bann.getConfig().isIgnoreSilent())
                return;

            handleEntry(entry, false);
        }

        @Override
        public void entryRemoved(final Entry entry) {
            if (entry.getType().equals("kick"))
                return;

            if (entry.isSilent() && bann.getConfig().isIgnoreSilent())
                return;

            handleEntry(entry, true);
        }

        public void handleEntry(final Entry entry, final boolean revoked) {
            final PunishmentAction punishment = new PunishmentAction();

            punishment.setId(String.valueOf(entry.getId()));

            switch (entry.getType()) {
            case "ban":
                if (entry.isIpban()) {
                    punishment.setType(revoked ? Type.UNBANIP : entry.isPermanent() ? Type.BANIP : Type.TEMPBANIP);
                } else {
                    punishment.setType(revoked ? Type.UNBAN : entry.isPermanent() ? Type.BAN : Type.TEMPBAN);
                }
                break;
            case "mute":
                punishment.setType(revoked ? Type.UNMUTE : entry.isPermanent() ? Type.MUTE : Type.TEMPMUTE);
                break;
            case "warn":
                punishment.setType(revoked ? Type.UNWARN : entry.isPermanent() ? Type.WARN : Type.TEMPWARN);
                break;
            case "kick":
                punishment.setType(Type.KICK);
                break;
            default:
                bann.getLogger().severe("Unknown punishment type '" + entry.getType() + "'.");
                return;
            }

            final String name = LiteBans.this.getName(entry.getUuid());

            if (name == null) {
                bann.getLogger().severe("Couldn't fetch player name from UUID '" + entry.getUuid() + "'. The message was not sent.");
                return;
            }

            boolean isConsole = entry.getExecutorName() == null
                    || "Console".equalsIgnoreCase(entry.getExecutorName());

            String operator = isConsole
                    ? bann.getConfig().getConsoleName()
                    : entry.getExecutorName();

            punishment.setPlayer(name);
            punishment.setOperator(operator);

            if (revoked) {
                if (!entry.isPermanent() && entry.isExpired(System.currentTimeMillis())) {
                    // automatic
                    punishment.setOperator(bann.getConfig().getExpiredOperatorName());
                }
            } else {
                punishment.setPermanent(entry.isPermanent());
                punishment.setReason(entry.getReason());
                punishment.setDuration(entry.getDurationString());
            }

            bann.handlePunishmentAction(punishment);
        }
    }
}
