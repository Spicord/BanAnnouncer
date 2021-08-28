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
import eu.mcdb.ban_announcer.config.Config;
import org.spicord.api.addon.SimpleAddon;
import org.spicord.bot.DiscordBot;
import org.spicord.bot.command.DiscordBotCommand;
import net.dv8tion.jda.api.Permission;

public final class BanAnnouncerAddon extends SimpleAddon {

    private BanAnnouncer announcer;

    public BanAnnouncerAddon(BanAnnouncer announcer) {
        super("BanAnnouncer", "ban_announcer", "Sheidy", "2.3.0", new String[] { "bareload" });
        this.announcer = announcer;
    }

    @Override
    public void onLoad(DiscordBot bot) {
        announcer.addBot(bot);
    }

    @Override
    public void onShutdown(DiscordBot bot) {
        announcer.removeBot(bot);
    }

    @Override
    public void onCommand(DiscordBotCommand command, String[] args) {
        if (command.getSender().hasPermission(Permission.MANAGE_CHANNEL)) {
            Config.getInstance().reload();
            command.reply("Successfully reloaded BanAnnouncer");
        } else {
            command.reply(command.getAuthorAsMention() + ", you do not have enough permission to run this command.");
        }
    }
}
