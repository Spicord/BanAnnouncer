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

package me.tini.announcer.bungee;

import java.io.File;

import org.spicord.Spicord;
import org.spicord.SpicordLoader;

import me.tini.announcer.BanAnnouncer;
import me.tini.announcer.BanAnnouncerPlugin;
import me.tini.announcer.PunishmentListeners;
import me.tini.announcer.ReloadCommand;
import me.tini.announcer.addon.BanAnnouncerAddon;
import me.tini.announcer.bungee.listener.AdvancedBanListener;
import me.tini.announcer.config.Config;
import me.tini.announcer.extension.Extension;
import me.tini.announcer.listener.LibertyBansListener;
import me.tini.announcer.listener.LiteBansListener;
import net.md_5.bungee.api.plugin.Plugin;

public final class BanAnnouncerBungee extends Plugin implements BanAnnouncerPlugin {

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
        pm.addNew("AdvancedBan", "advancedban", () -> new AdvancedBanListener(this), true, "me.leoko.advancedban.Universal");
        pm.addNew("LiteBans"   , "litebans"   , () -> new LiteBansListener(this)   , true, "litebans.api.Events");
        pm.addNew("LibertyBans", "libertybans", () -> new LibertyBansListener(this), true, "space.arim.libertybans.api.LibertyBans");

        for (Extension ext : announcer.getExtensions()) {
            pm.addNew(ext.getName(), ext.getKey(), ext.getInstanceSupplier(this), ext.isPunishmentManager(), ext.getRequiredClass());
        }

        final String pun = config.getPunishmentManager().toLowerCase();

        if ("auto".equals(pun)) {
            pm.autoDetect();
        } else {
            pm.startPunishListener(pun);
        }

        final String jail = config.getJailManager().toLowerCase();

        if (config.isJailManagerEnabled()) { // Jail enabled
            pm.startJailListener(jail);
        }

        spicord.getAddonManager().registerAddon(new BanAnnouncerAddon(this));
    }

    public BanAnnouncer getAnnouncer() {
        return announcer;
    }

    @Override
    public PunishmentListeners getPunishmentListeners() {
        return pm;
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
