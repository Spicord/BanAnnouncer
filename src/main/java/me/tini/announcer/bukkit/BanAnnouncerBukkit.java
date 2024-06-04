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

package me.tini.announcer.bukkit;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import org.spicord.Spicord;
import org.spicord.SpicordLoader;

import me.tini.announcer.BanAnnouncer;
import me.tini.announcer.BanAnnouncerPlugin;
import me.tini.announcer.PunishmentListeners;
import me.tini.announcer.ReloadCommand;
import me.tini.announcer.addon.BanAnnouncerAddon;
import me.tini.announcer.config.Config;
import me.tini.announcer.extension.ExtensionInfo;
import me.tini.announcer.extension.ExtensionContainer;
import me.tini.announcer.extension.impl.advancedban.AdvancedBanExtension;
import me.tini.announcer.extension.impl.betterjails.BetterJailsExtension;
import me.tini.announcer.extension.impl.essentialsjail.EssentialsJailExtension;
import me.tini.announcer.extension.impl.libertybans.LibertyBansExtension;
import me.tini.announcer.extension.impl.litebans.LiteBansExtension;
import me.tini.announcer.extension.impl.maxbans.MaxBansExtension;

public class BanAnnouncerBukkit extends JavaPlugin implements BanAnnouncerPlugin {

    private BanAnnouncer announcer;
    private PunishmentListeners pm;

    @Override
    public void onEnable() {
        SpicordLoader.addStartupListener(this::onSpicordLoad);
    }

    private void onSpicordLoad(Spicord spicord) {
        Config config = new Config(this);

        new ReloadCommand().register(this);

        this.announcer = new BanAnnouncer(config, spicord);

        this.announcer.loadExtensions(new File(getDataFolder(), "extensions"));

        if (pm != null) {
            pm.stopAllListeners();
        }

        pm = new PunishmentListeners(getLogger());

        // General punishments
        pm.addNew("AdvancedBan", "advancedban", () -> new AdvancedBanExtension(this), true, "me.leoko.advancedban.Universal");
        pm.addNew("LiteBans"   , "litebans"   , () -> new LiteBansExtension(this)   , true, "litebans.api.Events");
        pm.addNew("LibertyBans", "libertybans", () -> new LibertyBansExtension(this), true, "space.arim.libertybans.api.LibertyBans");
        pm.addNew("MaxBansPlus", "maxbans"    , () -> new MaxBansExtension(this)    , true, "org.maxgamer.maxbans.MaxBansPlus");

        // Jail
        pm.addNew("BetterJails", "betterjails", () -> new BetterJailsExtension(this)   , false, "com.github.fefo.betterjails.api.BetterJails");
        pm.addNew("EssentialsX", "essentials" , () -> new EssentialsJailExtension(this), false, "net.ess3.api.events.JailStatusChangeEvent");

        for (ExtensionContainer loader : announcer.getExtensions()) {
            ExtensionInfo ext = loader.getInfo();
            pm.addNew(ext.getName(), ext.getKey(), loader.getInstanceSupplier(this), ext.isPunishmentManager(), ext.getRequiredClass());
        }

        String pun = config.getPunishmentManager().toLowerCase();

        if ("auto".equals(pun)) {
            pm.autoDetect();
        } else {
            pm.startPunishListener(pun);
        }

        String jail = config.getJailManager().toLowerCase();

        if (config.isJailManagerEnabled()) { // Jail enabled
            pm.startJailListener(jail);
        }

        spicord.getAddonManager().registerAddon(new BanAnnouncerAddon(this));
    }

    @Override
    public BanAnnouncer getAnnouncer() {
        return announcer;
    }

    @Override
    public PunishmentListeners getPunishmentListeners() {
        return pm;
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

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }
}
