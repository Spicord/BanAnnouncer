/*
 * Copyright (C) 2019  OopsieWoopsie
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package eu.mcdb.ban_announcer;

import java.lang.reflect.Method;

public class Messages {

	public final String KICK;
	public final String BAN;
	public final String TEMPBAN;
	public final String MUTE;
	public final String TEMPMUTE;
	public final String BANIP;
	public final String TEMPBANIP;
	public final String WARN;
	public final String TEMPWARN;

	private final Object config;
	private final Method getStringMethod;

	protected Messages(Object config, Method getStringMethod) {
		this.config = config;
		this.getStringMethod = getStringMethod;

		this.KICK      = getString("kick");
		this.BAN       = getString("ban");
		this.TEMPBAN   = getString("tempban");
		this.MUTE      = getString("mute");
		this.TEMPMUTE  = getString("tempmute");
		this.BANIP     = getString("banip");
		this.TEMPBANIP = getString("tempbanip");
		this.WARN      = getString("warn");
		this.TEMPWARN  = getString("tempwarn");
	}

	private String getString(String key) {
		try {
			return (String) getStringMethod.invoke(config, "messages." + key);
		} catch (Exception e) {
			System.out.println("err: " + key);
			e.printStackTrace();
		}
		return "";
	}
}