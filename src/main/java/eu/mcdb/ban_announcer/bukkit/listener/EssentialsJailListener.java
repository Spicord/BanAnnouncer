package eu.mcdb.ban_announcer.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.ban_announcer.PunishmentAction;
import eu.mcdb.ban_announcer.PunishmentAction.Type;
import eu.mcdb.ban_announcer.bukkit.BanAnnouncerBukkit;
import net.ess3.api.IUser;
import net.ess3.api.events.JailStatusChangeEvent;

public class EssentialsJailListener implements Listener {

    private BanAnnouncerBukkit pl;

    public EssentialsJailListener(BanAnnouncerBukkit pl) {
        this.pl = pl;
    }

    @EventHandler
    public void onJailStatusChange(JailStatusChangeEvent event) {
        boolean gotJailed = event.getValue();

        IUser user = event.getAffected();
        IUser staff = event.getController();

        pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, () -> {
            PunishmentAction pun = new PunishmentAction();

            pun.setType(gotJailed ? Type.JAIL : Type.UNJAIL);
            pun.setJail(user.getJail());
            pun.setPlayer(user.getName());
            pun.setOperator(staff.getName());

            BanAnnouncer.getInstance().handlePunishmentAction(pun);
        }, 20);
    }
}
