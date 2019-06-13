package eu.mcdb.ban_announcer.addon;

import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.spicord.api.addon.SimpleAddon;
import eu.mcdb.spicord.bot.DiscordBot;

public class BanAnnouncerAddon extends SimpleAddon {

	private BanAnnouncer ba;

	public BanAnnouncerAddon(BanAnnouncer ba) {
		super("Ban Announcer", "ban_announcer", "OopsieWoopsie");
		this.ba = ba;
	}

	@Override
	public void onLoad(DiscordBot bot) {
		ba.addBot(bot);
	}
}