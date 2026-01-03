package me.tini.announcer.extension.impl.ultrapunishments;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import me.TechsCode.UltraPunishments.UltraPunishments;
import me.TechsCode.UltraPunishments.UltraPunishmentsAPI;
import me.TechsCode.UltraPunishments.event.BanEvent;
import me.TechsCode.UltraPunishments.event.FreezeEvent;
import me.TechsCode.UltraPunishments.event.KickEvent;
import me.TechsCode.UltraPunishments.event.ReportEvent;
import me.TechsCode.UltraPunishments.event.UnbanEvent;
import me.TechsCode.UltraPunishments.event.UnfreezeEvent;
import me.TechsCode.UltraPunishments.storage.types.IndexedPlayer;
import me.TechsCode.UltraPunishments.storage.types.Punishment;
import me.tini.announcer.PunishmentInfo;
import me.tini.announcer.plugin.bukkit.BanAnnouncerBukkit;
import me.tini.announcer.plugin.bukkit.BukkitPunishmentListener;
import me.tini.announcer.utils.TimeUtils;

public final class UltraPunishmentsListener extends BukkitPunishmentListener {

    private UltraPunishmentsAPI api;

    public UltraPunishmentsListener(BanAnnouncerBukkit plugin) {
        super(plugin);

        api = UltraPunishments.getApi();
    }

    @EventHandler
    public void onBan(final BanEvent event) {
        final PunishmentInfo punishment = new PunishmentInfo();
        punishment.setType(PunishmentInfo.Type.BAN);
        punishment.setPermanent(true);
        punishment.setReason(event.getReason());
        punishment.setOperator(event.getIssuer().getName());
        punishment.setPlayer(event.getPlayer().getName());
        punishment.setPlayerId(event.getPlayer().getUniqueId().toString());

        final IndexedPlayer indexedPlayer;
        if (event.getPlayer() instanceof Player) {
            indexedPlayer = api.getPlayerIndexes().get((Player) event.getPlayer()).get();
        } else {
            indexedPlayer = api.getPlayerIndexes().get(event.getPlayer().getUniqueId()).get();
        }

        // Execute on next server tick just to make sure "UP" has recorded the punishment.
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            List<Punishment> list = new LinkedList<>(api.getPunishments().target(indexedPlayer));

            // Order by date (from old to new).
            list.sort((a, b) -> {
                return (int)(a.getDate() - b.getDate());
            });

            // Get the last punishment (and assume this entry represents this event).
            Punishment last = list.get(list.size() - 1);

            if (!last.isPermanent()) {
                punishment.setType(PunishmentInfo.Type.TEMPBAN);
                punishment.setPermanent(false);

                String duration = TimeUtils.parseMillis(last.getFullDuration());

                punishment.setDuration(duration);
                punishment.setDateStart(last.getDate());
                punishment.setDateEnd(last.getExpiry());
            }

            super.handlePunishment(punishment);
        });
    }

    @EventHandler
    public void onUnban(UnbanEvent event) {
        final PunishmentInfo punishment = new PunishmentInfo();
        punishment.setType(PunishmentInfo.Type.UNBAN);
        punishment.setOperator(event.getIssuer().getName());
        punishment.setPlayer(event.getPlayer().getName());
        punishment.setPlayerId(event.getPlayer().getUniqueId().toString());

        handlePunishment(punishment);
    }

    @EventHandler
    public void onKick(KickEvent event) {
        final PunishmentInfo punishment = new PunishmentInfo();
        punishment.setType(PunishmentInfo.Type.KICK);
        punishment.setReason(event.getReason());
        punishment.setOperator(event.getIssuer().getName());
        punishment.setPlayer(event.getPlayer().getName());
        punishment.setPlayerId(event.getPlayer().getUniqueId().toString());

        handlePunishment(punishment);
    }

    @EventHandler
    public void onReport(ReportEvent event) {
        // Currently not handled
    }

    @EventHandler
    public void onFreeze(FreezeEvent event) {
        // Currently not handled
    }

    @EventHandler
    public void onUnfreeze(UnfreezeEvent event) {
        // Currently not handled
    }
}
