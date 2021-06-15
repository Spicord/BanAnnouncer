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

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.ban_announcer.bukkit.listener.AdvancedBanListener;
import eu.mcdb.ban_announcer.bukkit.listener.BetterJailsListener;
import eu.mcdb.ban_announcer.bukkit.listener.EssentialsJailListener;
import eu.mcdb.ban_announcer.config.Config;
import eu.mcdb.ban_announcer.listener.LiteBans;
import eu.mcdb.ban_announcer.listener.MaxBansPlus;
import org.spicord.Spicord;
import org.spicord.SpicordLoader;

public class BanAnnouncerBukkit extends JavaPlugin {

    private BanAnnouncer banAnnouncer;

    private BetterJailsListener betterJailsListener;

    @Override
    public void onEnable() {
        SpicordLoader.addStartupListener(this::enable);
    }

    private void enable(Spicord s) {
        Config config = new Config(getFile(), getDataFolder());

        this.banAnnouncer = new BanAnnouncer(config, getLogger());

        switch (config.getJailManager().toLowerCase()) {
        case "off": break;
        case "essentials":
            if (essentialsPresent()) {
                getServer().getPluginManager().registerEvents(new EssentialsJailListener(this), this);
            } else {
                getLogger().warning("[JailManager] EssentialsX is not present");
            }
            break;
        case "betterjails":
            if (betterjailsPresent()) {
                betterJailsListener = new BetterJailsListener(this);
                betterJailsListener.subscribe();
            } else {
                getLogger().warning("[JailManager] BetterJails is not present");
            }
        default:
            break;
        }

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

    private boolean essentialsPresent() {
        return isClassPresent("net.ess3.api.events.JailStatusChangeEvent");
    }

    private boolean betterjailsPresent() {
        return isClassPresent("com.github.fefo.betterjails.api.BetterJails");
    }

    private boolean usingLiteBans() {
        return isClassPresent("litebans.api.Events");
    }

    private boolean usingAdvancedBan() {
        return isClassPresent("me.leoko.advancedban.Universal");
    }

    private boolean usingMaxBans() {
        return isClassPresent("org.maxgamer.maxbans.MaxBansPlus");
    }

    private boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Exception e) {}
        return false;
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        if (betterJailsListener != null) {
            betterJailsListener.unsubscribe();
        }

        if (banAnnouncer != null) {
            banAnnouncer.disable();
            banAnnouncer = null;
        }
    }
}
