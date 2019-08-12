package eu.mcdb.ban_announcer.bungee.listener;

import eu.mcdb.ban_announcer.listener.AdvancedBan;
import me.leoko.advancedban.bungee.event.PunishmentEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class AdvancedBanListener implements Listener {

	@EventHandler
	public void onPunishment(PunishmentEvent event) {
        AdvancedBan.onPunishment(event.getPunishment());
	}
}
