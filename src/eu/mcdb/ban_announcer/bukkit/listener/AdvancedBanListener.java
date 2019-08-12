package eu.mcdb.ban_announcer.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import eu.mcdb.ban_announcer.listener.AdvancedBan;
import me.leoko.advancedban.bukkit.event.PunishmentEvent;

public final class AdvancedBanListener implements Listener {

    @EventHandler
    public void onPunishment(PunishmentEvent event) {
        AdvancedBan.onPunishment(event.getPunishment());
    }
}
