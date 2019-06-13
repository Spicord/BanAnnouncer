package eu.mcdb.ban_announcer.bukkit.listener;

import eu.mcdb.ban_announcer.BanAnnouncer;
import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;

public class AdvancedBanListener implements Listener {

	@EventHandler
	public void onPunishment(PunishmentEvent event) {
		BanAnnouncer.getInstance().onPunishment(event.getPunishment());
	}
}