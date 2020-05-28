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

package eu.mcdb.ban_announcer.addon;

import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.spicord.api.addon.SimpleAddon;
import eu.mcdb.spicord.bot.DiscordBot;

public final class BanAnnouncerAddon extends SimpleAddon {

    private BanAnnouncer ba;

    public BanAnnouncerAddon(BanAnnouncer ba) {
        super("BanAnnouncer", "ban_announcer", "Sheidy");
        this.ba = ba;
    }

    @Override
    public void onLoad(DiscordBot bot) {
        ba.addBot(bot);
    }
}
