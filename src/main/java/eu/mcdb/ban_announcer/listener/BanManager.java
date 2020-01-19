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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import eu.mcdb.ban_announcer.PunishmentAction;
import eu.mcdb.ban_announcer.PunishmentAction.Type;
import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.ban_announcer.utils.TimeUtils;
import me.confuser.banmanager.bukkit.api.events.IpBannedEvent;
import me.confuser.banmanager.bukkit.api.events.IpUnbanEvent;
import me.confuser.banmanager.bukkit.api.events.PlayerBannedEvent;
import me.confuser.banmanager.bukkit.api.events.PlayerMutedEvent;
import me.confuser.banmanager.bukkit.api.events.PlayerUnbanEvent;
import me.confuser.banmanager.bukkit.api.events.PlayerUnmuteEvent;
import me.confuser.banmanager.bukkit.api.events.PlayerWarnedEvent;
import me.confuser.banmanager.common.data.PlayerBanData;

// no priority - 12 Nov 2019
public final class BanManager implements Listener {

    private final BanAnnouncer banAnnouncer;

    public BanManager() {
        this.banAnnouncer = BanAnnouncer.getInstance();
    }

    @EventHandler
    public void onIpBan(IpBannedEvent event) {
        // TODO
    }

    @EventHandler
    public void onIpUnban(IpUnbanEvent event) {} // soon

    @EventHandler
    public void onPlayerBan(PlayerBannedEvent event) {
        PlayerBanData ban = event.getBan();
        PunishmentAction punishment = new PunishmentAction();
        punishment.setPlayer(ban.getPlayer().getName());
        punishment.setOperator(ban.getActor().getName());
        punishment.setPermanent(ban.getExpires() <= 0);
        punishment.setType(punishment.isPermanent() ? Type.BAN : Type.TEMPBAN);
        punishment.setReason(ban.getReason());
        punishment.setDuration(getDuration(ban.getExpires()));
        banAnnouncer.handlePunishmentAction(punishment);
    }

    private String getDuration(long expires) {
        if (expires <= 0) return "permanent";
        expires = (expires - (System.currentTimeMillis() / 1000L)) * 1000L;
        return TimeUtils.parseMillis(expires);
    }

    @EventHandler
    public void onPlayerUnban(PlayerUnbanEvent event) {} // soon

    @EventHandler
    public void onPlayerMute(PlayerMutedEvent event) {
        // TODO
    }

    @EventHandler
    public void onPlayerUnmute(PlayerUnmuteEvent event) {} // soon

    @EventHandler
    public void onPlayerWarn(PlayerWarnedEvent event) {
        // TODO
    }

    // missing kick
    // missing unwarn
}
