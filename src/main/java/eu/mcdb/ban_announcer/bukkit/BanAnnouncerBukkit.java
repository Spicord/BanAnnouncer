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

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import org.spicord.Spicord;
import org.spicord.SpicordLoader;

import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.ban_announcer.BanAnnouncerPlugin;
import eu.mcdb.ban_announcer.PunishmentListeners;
import eu.mcdb.ban_announcer.bukkit.listener.AdvancedBanListener;
import eu.mcdb.ban_announcer.bukkit.listener.BetterJailsListener;
import eu.mcdb.ban_announcer.bukkit.listener.EssentialsJailListener;
import eu.mcdb.ban_announcer.bukkit.listener.MaxBansListener;
import eu.mcdb.ban_announcer.config.Config;
import eu.mcdb.ban_announcer.extension.Extension;
import eu.mcdb.ban_announcer.listener.LiteBansListener;

public class BanAnnouncerBukkit extends JavaPlugin implements BanAnnouncerPlugin {

    private BanAnnouncer announcer;
    private PunishmentListeners pm;

    @Override
    public void onEnable() {
        SpicordLoader.addStartupListener(this::onSpicordLoad);
    }

    private void onSpicordLoad(Spicord spicord) {
        Config config = new Config(this);

        this.announcer = new BanAnnouncer(config, spicord);

        this.announcer.loadExtensions(new File(getDataFolder(), "extensions"));

        pm = new PunishmentListeners(getLogger());

        // General punishments
        pm.addNew("AdvancedBan", "advancedban", () -> new AdvancedBanListener(this)   , true, "me.leoko.advancedban.Universal");
        pm.addNew("LiteBans"   , "litebans"   , () -> new LiteBansListener(this)      , true, "litebans.api.Events");
        pm.addNew("MaxBansPlus", "maxbans"    , () -> new MaxBansListener(this)       , true, "org.maxgamer.maxbans.MaxBansPlus");

        // Jail
        pm.addNew("BetterJails", "betterjails", () -> new BetterJailsListener(this)   , false, "com.github.fefo.betterjails.api.BetterJails");
        pm.addNew("EssentialsX", "essentials" , () -> new EssentialsJailListener(this), false, "net.ess3.api.events.JailStatusChangeEvent");

        for (Extension ext : announcer.getExtensions()) {
            pm.addNew(ext.getName(), ext.getKey(), ext.getInstanceSupplier(this), ext.isPunishmentManager(), ext.getRequiredClass());
        }

        String pun = config.getPunishmentManager().toLowerCase();

        if ("auto".equals(pun)) {
            pm.autoDetect();
        } else {
            pm.startListener(pun);
        }

        String jail = config.getJailManager().toLowerCase();

        if (!"off".equals(jail)) { // Jail enabled
            pm.startListener(jail);
        }
    }

    @Override
    public BanAnnouncer getAnnouncer() {
        return announcer;
    }

    @Override
    public File getFile() {
        return super.getFile();
    }

    @Override
    public void onDisable() {
        if (pm != null) {
            pm.stopAllListeners();
        }
        if (announcer != null) {
            announcer.disable();
            announcer = null;
        }
    }
}
