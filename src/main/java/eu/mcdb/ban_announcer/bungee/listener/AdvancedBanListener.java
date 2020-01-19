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

package eu.mcdb.ban_announcer.bungee.listener;

import eu.mcdb.ban_announcer.listener.AdvancedBan;
import me.leoko.advancedban.bungee.event.PunishmentEvent;
import me.leoko.advancedban.bungee.event.RevokePunishmentEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class AdvancedBanListener implements Listener {

    @EventHandler
    public void onPunishment(PunishmentEvent event) {
        AdvancedBan.onPunishmentAction(event.getPunishment(), false);
    }

    @EventHandler
    public void onRevokePunishment(RevokePunishmentEvent event) {
        AdvancedBan.onPunishmentAction(event.getPunishment(), true);
    }
}
