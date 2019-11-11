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

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.maxgamer.maxbans.event.BanAddressEvent;
import org.maxgamer.maxbans.event.BanUserEvent;
import org.maxgamer.maxbans.event.KickUserEvent;
import org.maxgamer.maxbans.event.MaxBansRestrictEvent;
import org.maxgamer.maxbans.event.MuteAddressEvent;
import org.maxgamer.maxbans.event.MuteUserEvent;
import org.maxgamer.maxbans.event.UnbanAddressEvent;
import org.maxgamer.maxbans.event.UnbanUserEvent;
import org.maxgamer.maxbans.event.UnmuteAddressEvent;
import org.maxgamer.maxbans.event.UnmuteUserEvent;
import org.maxgamer.maxbans.event.WarnUserEvent;
import org.maxgamer.maxbans.orm.Restriction;
import org.maxgamer.maxbans.util.TemporalDuration;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.mcdb.ban_announcer.BAPunishment;
import eu.mcdb.ban_announcer.BAPunishment.Type;
import eu.mcdb.ban_announcer.BanAnnouncer;

public class MaxBansPlus implements Listener {

    private final BanAnnouncer banAnnouncer;
    private final Cache<String, String> kickReasons;

    public MaxBansPlus() {
        this.banAnnouncer = BanAnnouncer.getInstance();
        this.kickReasons = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .initialCapacity(10)
                .build();
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();

        // syntax: /kick <player> [reason]
        if (command.startsWith("/kick") || command.startsWith("/maxbans:kick")) {
            if (event.getPlayer().hasPermission("maxbans.kick")) {
                int index = command.indexOf(' ');

                if (index == -1) return; // no parameters

                command = command.substring(index).trim();

                index = command.indexOf(' ');

                String player, reason;

                if (index == -1) { // no reason/message
                    player = command;
                    reason = "none";
                } else {
                    player = command.substring(0, index);
                    reason = command.substring(index).trim();
                }

                kickReasons.put(player.toLowerCase(), reason);
            }
        }
    }

    @EventHandler
    public void onBanAddress(BanAddressEvent event) {
        handleRestriction(event, event.getBan(), Type.BANIP, Type.TEMPBANIP);
    }

    @EventHandler
    public void onBanUser(BanUserEvent event) {
        handleRestriction(event, event.getBan(), Type.BAN, Type.TEMPBAN);
    }

    @EventHandler
    public void onKickUser(KickUserEvent event) {
        BAPunishment punishment = new BAPunishment(Type.KICK);
        String staff = event.getSource().getName();
        String player = event.getTarget().getName();
        String reason = kickReasons.getIfPresent(player.toLowerCase());
        reason = reason == null ? "none" : reason;
        punishment.setReason(reason);
        punishment.setOperator(staff);
        punishment.setPlayer(player);
        banAnnouncer.handlePunishment(punishment);
    }

    @EventHandler
    public void onMuteAddress(MuteAddressEvent event) {} // unused

    @EventHandler
    public void onMuteUser(MuteUserEvent event) {
        handleRestriction(event, event.getMute(), Type.MUTE, Type.TEMPMUTE);
    }

    @EventHandler
    public void onUnbanAddress(UnbanAddressEvent event) {} // unused

    @EventHandler
    public void onUnbanUser(UnbanUserEvent event) {} // unused

    @EventHandler
    public void onUnmuteAddress(UnmuteAddressEvent event) {} // unused

    @EventHandler
    public void onUnmuteUser(UnmuteUserEvent event) {} // unused

    @EventHandler
    public void onWarnUser(WarnUserEvent event) {
        handleRestriction(event, event.getWarning(), Type.WARN, Type.TEMPWARN);
    }

    private void handleRestriction(MaxBansRestrictEvent<?> event, Restriction restriction, Type perm, Type temp) {
        BAPunishment punishment = new BAPunishment();
        punishment.setPlayer(event.getTarget().getName());
        punishment.setOperator(event.isPlayerAdministered() ? event.getAdmin().getName() : "Console");
        punishment.setPermanent(restriction.getExpiresAt() == null);
        punishment.setType(punishment.isPermanent() ? perm : temp);
        punishment.setReason(restriction.getReason());
        punishment.setDuration(getDuration(restriction));
        banAnnouncer.handlePunishment(punishment);
    }

    private String getDuration(Restriction restriction) {
        if (restriction.getExpiresAt() == null) return "permanent";
        long millis = (restriction.getExpiresAt().getEpochSecond() - restriction.getCreated().getEpochSecond()) * 1000;
        return new TemporalDuration(Duration.ofMillis(millis)).toString();
    }
}