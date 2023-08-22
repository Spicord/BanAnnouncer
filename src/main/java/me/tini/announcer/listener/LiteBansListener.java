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

package me.tini.announcer.listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import litebans.api.Database;
import litebans.api.Entry;
import litebans.api.Events;
import litebans.api.Events.Listener;
import me.tini.announcer.BanAnnouncerPlugin;
import me.tini.announcer.PunishmentAction;
import me.tini.announcer.PunishmentListener;
import me.tini.announcer.PunishmentAction.Type;

public final class LiteBansListener extends PunishmentListener {

    private final Events events;
    private final Database database;
    private final InternalListener theListener;

    public LiteBansListener(BanAnnouncerPlugin plugin) {
        super(plugin.getAnnouncer());
        this.events = Events.get();
        this.database = Database.get();
        this.theListener = new InternalListener();
    }

    @Override
    public void register() {
        events.register(theListener);
    }

    @Override
    public void unregister() {
        events.unregister(theListener);
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
            throw new IllegalStateException("Unknown punishment type '" + entry.getType() + "'.");
        }

        final String uuidDashed = entry.getUuid();
        
        punishment.setPlayerId(uuidDashed);
        
		final String name = getPlayerName(uuidDashed);

        if (name == null) {
            throw new IllegalStateException("Couldn't fetch player name from UUID '" + entry.getUuid() + "'. The message was not sent.");
        }

        boolean isConsole = entry.getExecutorName() == null
                || "Console".equalsIgnoreCase(entry.getExecutorName());

        String operator = isConsole
                ? getAnnouncer().getConfig().getConsoleName()
                : entry.getExecutorName();

        punishment.setPlayer(name);
        punishment.setOperator(operator);

        if (revoked) {
            if (!entry.isPermanent() && entry.isExpired(System.currentTimeMillis())) {
                // automatic
                punishment.setOperator(getAnnouncer().getConfig().getExpiredOperatorName());
            }
        } else {
            punishment.setPermanent(entry.isPermanent());
            punishment.setDuration(entry.getDurationString());
        }

        punishment.setReason(entry.getReason());

        if (!punishment.isPermanent()) {
        	punishment.setStart(entry.getDateStart());
        	punishment.setEnd(entry.getDateEnd());
        }

        punishment.setLitebansServerOrigin(entry.getServerOrigin());
        punishment.setLitebansServerScope(entry.getServerScope());
        punishment.setLitebansRandomId(entry.getRandomID());

        handlePunishment(punishment);
    }

    private String getPlayerName(final String uuid) {
        final String sentence = "SELECT name FROM {history} WHERE uuid = ? ORDER BY id DESC LIMIT 1";

        try (final PreparedStatement stmt = database.prepareStatement(sentence)) {
            stmt.setString(1, uuid);

            final ResultSet rs = stmt.executeQuery();

            if (rs.next())
                return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class InternalListener extends Listener {

        @Override
        public void entryAdded(final Entry entry) {
            if (!entry.isActive() && !entry.getType().equals("kick"))
                return;

            if (entry.isSilent() && getAnnouncer().getConfig().isIgnoreSilent())
                return;

            handleEntry(entry, false);
        }

        @Override
        public void entryRemoved(final Entry entry) {
            if (entry.getType().equals("kick"))
                return;

            if (entry.isSilent() && getAnnouncer().getConfig().isIgnoreSilent())
                return;

            handleEntry(entry, true);
        }
    }
}
