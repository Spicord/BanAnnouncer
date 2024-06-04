package me.tini.announcer.extension.impl.essentialsjail;

import org.bukkit.event.EventHandler;

import me.tini.announcer.PunishmentInfo;
import me.tini.announcer.PunishmentInfo.Type;
import me.tini.announcer.plugin.bukkit.BanAnnouncerBukkit;
import me.tini.announcer.plugin.bukkit.BukkitPunishmentListener;
import net.ess3.api.IUser;
import net.ess3.api.events.JailStatusChangeEvent;

public class EssentialsJailListener extends BukkitPunishmentListener {

    public EssentialsJailListener(BanAnnouncerBukkit plugin) {
        super(plugin);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onJailStatusChange(JailStatusChangeEvent event) {
        boolean gotJailed = event.getValue();

        IUser user = event.getAffected();
        IUser staff = event.getController();

        getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            PunishmentInfo punishment = new PunishmentInfo();

            punishment.setType(gotJailed ? Type.JAIL : Type.UNJAIL);
            punishment.setJail(user.getJail());
            punishment.setPlayer(user.getName());
            punishment.setOperator(staff == null ? getAnnouncer().getConfig().getConsoleName() : staff.getName());

            handlePunishment(punishment);
        }, 20);
    }
}
