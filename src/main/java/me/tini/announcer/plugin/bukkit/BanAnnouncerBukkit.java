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

package me.tini.announcer.plugin.bukkit;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import me.tini.announcer.BanAnnouncer;
import me.tini.announcer.BanAnnouncerPlugin;
import me.tini.announcer.ReloadCommand;
import me.tini.announcer.config.Config;
import me.tini.announcer.extension.impl.advancedban.AdvancedBanExtensionBukkit;
import me.tini.announcer.extension.impl.betterjails.BetterJailsExtension;
import me.tini.announcer.extension.impl.essentialsjail.EssentialsJailExtension;
import me.tini.announcer.extension.impl.libertybans.LibertyBansExtension;
import me.tini.announcer.extension.impl.litebans.LiteBansExtension;
import me.tini.announcer.extension.impl.maxbans.MaxBansExtension;
import me.tini.command.bukkit.IBukkitPlugin;

public class BanAnnouncerBukkit extends JavaPlugin implements BanAnnouncerPlugin, IBukkitPlugin {

    private BanAnnouncer announcer;

    @Override
    public void onEnable() {
        Config config = new Config(this);

        registerCommand("banannouncer-reload", new ReloadCommand());

        announcer = BanAnnouncer.build(this, config);

        announcer.loadExtensions(new File(getDataFolder(), "extensions"));

        announcer.registerExtension("AdvancedBan", "advancedban", () -> new AdvancedBanExtensionBukkit(this), "me.leoko.advancedban.Universal");
        announcer.registerExtension("LiteBans"   , "litebans"   , () -> new LiteBansExtension(this)      , "litebans.api.Events");
        announcer.registerExtension("LibertyBans", "libertybans", () -> new LibertyBansExtension(this)   , "space.arim.libertybans.api.LibertyBans");
        announcer.registerExtension("MaxBansPlus", "maxbans"    , () -> new MaxBansExtension(this)       , "org.maxgamer.maxbans.MaxBansPlus");
        announcer.registerExtension("BetterJails", "betterjails", () -> new BetterJailsExtension(this)   , "com.github.fefo.betterjails.api.BetterJails");
        announcer.registerExtension("EssentialsX", "essentials" , () -> new EssentialsJailExtension(this), "net.ess3.api.events.JailStatusChangeEvent");

        announcer.enableExtensions();

        announcer.initialize();
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
