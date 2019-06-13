package eu.mcdb.ban_announcer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import eu.mcdb.ban_announcer.addon.BanAnnouncerAddon;
import eu.mcdb.spicord.Spicord;
import eu.mcdb.spicord.bot.DiscordBot;
import me.leoko.advancedban.utils.Punishment;
import net.dv8tion.jda.core.entities.TextChannel;

public class BanAnnouncer {

	private static BanAnnouncer instance;
	private final Logger logger;
	private final Set<DiscordBot> bots;
	private boolean enabled = true;

	public BanAnnouncer(Logger logger) {
		instance = this;
		this.logger = logger;
		this.bots = Collections.synchronizedSet(new HashSet<DiscordBot>());
		Spicord.getInstance().getAddonManager().registerAddon(new BanAnnouncerAddon(this));
	}

	public void onPunishment(Punishment punishment) {
		if (!enabled) return;

		String player   = punishment.getName();
		String operator = punishment.getOperator();
		String reason   = punishment.getReason();
		String duration = punishment.getDuration(true);

		switch (punishment.getType()) {
		case BAN:
		case TEMP_BAN:
			sendBanMessage(player, operator, reason, duration);
			break;
		case KICK:
			sendKickMessage(player, operator, reason);
			break;
		case MUTE:
		case TEMP_MUTE:
			sendMuteMessage(player, operator, reason, duration);
			break;
		case WARNING:
		case TEMP_WARNING:
			sendWarnMessage(player, operator, reason, duration);
			break;
		case IP_BAN:
		case TEMP_IP_BAN:
			sendIpBanMessage(player, operator, reason, duration);
			break;
		default:
			break;
		}
	}

	private void sendKickMessage(String player, String operator, String reason) {
		sendDiscordMessage(
				new MessageFormatter(Config.MESSAGES.KICK)
					.addPlaceholder("player", player)
					.addPlaceholder("staff", operator)
					.addPlaceholder("reason", reason)
					.toString());
	}

	private void sendIpBanMessage(String player, String operator, String reason, String duration) {
		sendDiscordMessage(
				new MessageFormatter(duration.equals("permanent")
						? Config.MESSAGES.BANIP
						: Config.MESSAGES.TEMPBANIP)
					.addPlaceholder("player", player)
					.addPlaceholder("staff", operator)
					.addPlaceholder("reason", reason)
					.addPlaceholder("duration", duration)
					.toString());
	}

	private void sendWarnMessage(String player, String operator, String reason, String duration) {
		sendDiscordMessage(
				new MessageFormatter(duration.equals("permanent")
						? Config.MESSAGES.WARN
						: Config.MESSAGES.TEMPWARN)
					.addPlaceholder("player", player)
					.addPlaceholder("staff", operator)
					.addPlaceholder("reason", reason)
					.addPlaceholder("duration", duration)
					.toString());
	}

	private void sendBanMessage(String player, String operator, String reason, String duration) {
		sendDiscordMessage(
				new MessageFormatter(
					duration.equals("permanent")
						? Config.MESSAGES.BAN
						: Config.MESSAGES.TEMPBAN)
					.addPlaceholder("player", player)
					.addPlaceholder("staff", operator)
					.addPlaceholder("reason", reason)
					.addPlaceholder("duration", duration)
					.toString());
	}

	private void sendMuteMessage(String player, String operator, String reason, String duration) {
		sendDiscordMessage(
				new MessageFormatter(
					duration.equals("permanent")
						? Config.MESSAGES.MUTE
						: Config.MESSAGES.TEMPMUTE)
					.addPlaceholder("player", player)
					.addPlaceholder("staff", operator)
					.addPlaceholder("reason", reason)
					.addPlaceholder("duration", duration)
					.toString());
	}

	private void sendDiscordMessage(String message) {
		if (message == null) return;

		bots.stream()
				.filter(DiscordBot::isReady)
				.map(DiscordBot::getJda)
				.forEach(jda -> {
					Config.CHANNELS_TO_ANNOUNCE.forEach(channelId -> {
						TextChannel channel = jda.getTextChannelById(channelId);

						if (channel == null) {
							getLogger().severe("Cannot find the channel with id '" + channelId + "'. The message was not sent.");
						} else {
							channel.sendMessage(message).queue();
						}
					});
				});
	}

	public void addBot(DiscordBot bot) {
		bots.add(bot);
	}

	public static BanAnnouncer getInstance() {
		return instance;
	}

	public void disable() {
		enabled  = false;
		instance = null;
		bots.clear();
	}

	public Logger getLogger() {
		return logger;
	}

	private class MessageFormatter {

		private final Map<String, String> map;
		private String message;

		public MessageFormatter(String message) {
			this.map = new HashMap<String, String>();
			this.message = message;
		}

		public MessageFormatter addPlaceholder(String key, String value) {
			map.put(key, value);
			return this;
		}

		@Override
		public String toString() {
			for (Entry<String, String> z : map.entrySet()) {
				message = message.replace("%" + z.getKey() + "%", z.getValue());
			}
			return message;
		}
	}
}