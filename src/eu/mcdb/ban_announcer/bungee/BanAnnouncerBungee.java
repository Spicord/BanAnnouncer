package eu.mcdb.ban_announcer.bungee;

import java.util.concurrent.TimeUnit;
import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.ban_announcer.Config;
import eu.mcdb.ban_announcer.bungee.listener.AdvancedBanListener;
import eu.mcdb.spicord.SpicordLoader.ServerType;
import net.md_5.bungee.api.plugin.Plugin;

public class BanAnnouncerBungee extends Plugin {

	private BanAnnouncer banAnnouncer;
	private static BanAnnouncerBungee instance;

	@Override
	public void onEnable() {
		instance = this;
		new Config(ServerType.BUNGEECORD);
		this.banAnnouncer = new BanAnnouncer(getLogger());
		getProxy().getScheduler().schedule(this, () -> enable(), 3, TimeUnit.SECONDS);
	}

	private void enable() {
		try {
			Class.forName("me.leoko.advancedban.Universal");
			getProxy().getPluginManager().registerListener(this, new AdvancedBanListener());
		} catch (Exception e) {
			getLogger().severe("You need to have AdvancedBan installed! BanAnnouncer will not work.");
		}
	}

	@Override
	public void onDisable() {
		banAnnouncer.disable();
		this.banAnnouncer = null;
	}

	public static BanAnnouncerBungee getInstance() {
		return instance;
	}
}