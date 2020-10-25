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

package eu.mcdb.ban_announcer.bukkit;

import static org.spicord.reflect.ReflectUtils.findClass;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.ban_announcer.bukkit.listener.AdvancedBanListener;
import eu.mcdb.ban_announcer.config.Config;
import eu.mcdb.ban_announcer.listener.LiteBans;
import eu.mcdb.ban_announcer.listener.MaxBansPlus;
import org.spicord.Spicord;
import org.spicord.event.SpicordEvent;

public class BanAnnouncerBukkit extends JavaPlugin {

    private BanAnnouncer banAnnouncer;

    @Override
    public void onEnable() {
        Spicord.getInstance().addEventListener(SpicordEvent.SPICORD_LOADED, this::enable);
    }

    private void enable(Spicord s) {
        Config config = new Config(getFile(), getDataFolder());

        this.banAnnouncer = new BanAnnouncer(config, getLogger());

        switch (config.getPunishmentManager().toLowerCase()) {
        case "auto":
            if (usingLiteBans()) {
                getLogger().info("[AutoDetect] Using LiteBans as the punishment manager.");
                new LiteBans(banAnnouncer);
            } else if (usingAdvancedBan()) {
                getLogger().info("[AutoDetect] Using AdvancedBan as the punishment manager.");
                getServer().getPluginManager().registerEvents(new AdvancedBanListener(), this);
            } else if (usingMaxBans()) {
                getLogger().info("[AutoDetect] Using MaxBansPlus as the punishment manager.");
                getServer().getPluginManager().registerEvents(new MaxBansPlus(), this);
            } else {
                getLogger().severe("[AutoDetect] No compatible plugin found. BanAnnouncer will not work!.");
            }
            break;
        case "advancedban":
            if (usingAdvancedBan()) {
                getLogger().info("Using AdvancedBan as the punishment manager.");
                getServer().getPluginManager().registerEvents(new AdvancedBanListener(), this);
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
            if (usingMaxBans()) {
                getLogger().info("Using MaxBansPlus as the punishment manager.");
                getServer().getPluginManager().registerEvents(new MaxBansPlus(), this);
            } else {
                getLogger().severe("You choose MaxBansPlus but you don't have it installed, BanAnnouncer will not work!.");
            }
            break;
        default:
            getLogger().severe("The punishment manager '" + config.getPunishmentManager()
                    + "' is not compatible with BanAnnouncer, you can request the integration"
                    + " with it on https://github.com/OopsieWoopsie/BanAnnouncer/issues");
            break;
        }
    }

    private boolean usingLiteBans() {
        return findClass("litebans.api.Events").isPresent();
    }

    private boolean usingAdvancedBan() {
        return findClass("me.leoko.advancedban.Universal").isPresent();
    }

    private boolean usingMaxBans() {
        return findClass("org.maxgamer.maxbans.MaxBansPlus").isPresent();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        if (banAnnouncer != null) {
            banAnnouncer.disable();
            banAnnouncer = null;
        }
    }
}
