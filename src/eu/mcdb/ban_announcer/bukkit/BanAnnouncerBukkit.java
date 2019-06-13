package eu.mcdb.ban_announcer.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.ban_announcer.Config;
import eu.mcdb.ban_announcer.bukkit.listener.AdvancedBanListener;
import eu.mcdb.spicord.SpicordLoader.ServerType;

public class BanAnnouncerBukkit extends JavaPlugin {

	private BanAnnouncer banAnnouncer;
	private static BanAnnouncerBukkit instance;

	@Override
	public void onEnable() {
		instance = this;
		new Config(ServerType.BUKKIT);
		this.banAnnouncer = new BanAnnouncer(getLogger());
		getServer().getScheduler().scheduleSyncDelayedTask(this, () -> enable(), 60);
	}

	private void enable() {
		try {
			Class.forName("me.leoko.advancedban.Universal");
			getServer().getPluginManager().registerEvents(new AdvancedBanListener(), this);
		} catch (Exception e) {
			getLogger().severe("You need to have AdvancedBan installed! BanAnnouncer will not work.");
		}
	}

	@Override
	public void onDisable() {
		banAnnouncer.disable();
		this.banAnnouncer = null;
	}

	public static BanAnnouncerBukkit getInstance() {
		return instance;
	}
}