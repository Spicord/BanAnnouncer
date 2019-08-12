package eu.mcdb.ban_announcer;

import lombok.Data;

@Data
public final class BAPunishment {

	private String player;
	private String operator;
	private String reason;
	private String duration;
	private boolean permanent = false;
	private Type type;

	public BAPunishment() {}

	public BAPunishment(Type type) {
		this.type = type;
	}

	public enum Type {
		KICK, BAN, TEMPBAN, MUTE, TEMPMUTE, BANIP, TEMPBANIP, WARN, TEMPWARN;
	}
}