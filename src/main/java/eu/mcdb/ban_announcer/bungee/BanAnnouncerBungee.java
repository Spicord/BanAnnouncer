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

package eu.mcdb.ban_announcer.bungee;

import org.spicord.reflect.ReflectUtils;
import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.ban_announcer.bungee.listener.AdvancedBanListener;
import eu.mcdb.ban_announcer.config.Config;
import eu.mcdb.ban_announcer.listener.LiteBans;
import org.spicord.Spicord;
import org.spicord.event.SpicordEvent;
import net.md_5.bungee.api.plugin.Plugin;

public final class BanAnnouncerBungee extends Plugin {

    private BanAnnouncer banAnnouncer;

    @Override
    public void onEnable() {
        Spicord.getInstance().addEventListener(SpicordEvent.SPICORD_LOADED, this::enable);
    }

    private void enable(Spicord s) {
        Config config = new Config(getFile(), getDataFolder());

        this.banAnnouncer = new BanAnnouncer(config, getLogger());

        switch (config.getPunishmentManager()) {
        case "auto":
            if (usingLiteBans()) {
                getLogger().info("[AutoDetect] Using LiteBans as the punishment manager.");
                new LiteBans(banAnnouncer);
            } else if (usingAdvancedBan()) {
                getLogger().info("[AutoDetect] Using AdvancedBan as the punishment manager.");
                getProxy().getPluginManager().registerListener(this, new AdvancedBanListener());
            } else {
                getLogger().severe("[AutoDetect] No compatible plugin found. BanAnnouncer will not work!.");
            }
            break;
        case "advancedban":
            if (usingAdvancedBan()) {
                getLogger().info("Using AdvancedBan as the punishment manager.");
                getProxy().getPluginManager().registerListener(this, new AdvancedBanListener());
            } else {
                getLogger().severe("You choose AdvancedBan but you don't have it installed, BanAnnouncer will not work!.");
            }
            break;
        case "litebans":
            if (usingLiteBans()) {
                getLogger().info("Using LiteBans as the punishment manager.");
                new LiteBans(banAnnouncer);
            } else {
                getLogger().severe("You choose LiteBans but you don't have it installed, BanAnnouncer will not work!.");
            }
            break;
        case "maxbans":
        case "maxbansplus":
            getLogger().severe("You choose MaxBansPlus but it only works on spigot/bukkit, BanAnnouncer will not work!.");
            break;
        default:
            getLogger().severe("The punishment manager '" + config.getPunishmentManager()
                    + "' is not compatible with BanAnnouncer, you can request the integration"
                    + " with it on https://github.com/OopsieWoopsie/BanAnnouncer/issues");
            break;
        }
    }

    private boolean usingLiteBans() {
        return ReflectUtils.findClass("litebans.api.Events").isPresent();
    }

    private boolean usingAdvancedBan() {
        return ReflectUtils.findClass("me.leoko.advancedban.Universal").isPresent();
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterListeners(this);

        if (banAnnouncer != null) {
            banAnnouncer.disable();
            banAnnouncer = null;
        }
    }
}
