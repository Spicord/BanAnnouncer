package me.tini.announcer.velocity;

import java.io.File;

import org.spicord.Spicord;
import org.spicord.SpicordLoader;
import org.spicord.plugin.VelocityPlugin;
import org.spicord.reflect.ReflectUtils;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import me.tini.announcer.BanAnnouncer;
import me.tini.announcer.BanAnnouncerPlugin;
import me.tini.announcer.PunishmentListeners;
import me.tini.announcer.ReloadCommand;
import me.tini.announcer.addon.BanAnnouncerAddon;
import me.tini.announcer.config.Config;
import me.tini.announcer.extension.Extension;
import me.tini.announcer.listener.LibertyBansListener;
import me.tini.announcer.listener.LiteBansListener;

@Plugin(
    id = "ban_announcer",
    name = "BanAnnouncer",
    version = "2.7",
    authors = { "Tini" },
    dependencies = {
        @Dependency(id = "spicord", optional = false),
        @Dependency(id = "libertybans", optional = true),
        @Dependency(id = "litebans", optional = true)
    }
)
public class BanAnnouncerVelocity extends VelocityPlugin implements BanAnnouncerPlugin {

    private BanAnnouncer announcer;
    private PunishmentListeners pm;
    private Config config;

    @Inject
    public BanAnnouncerVelocity(ProxyServer proxyServer) {
        super(proxyServer);
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        SpicordLoader.addStartupListener(this::onSpicordLoad);
    }

    private void onSpicordLoad(Spicord spicord) {
        config = new Config(this);

        new ReloadCommand().register(this);

        this.announcer = new BanAnnouncer(config, spicord);

        this.announcer.loadExtensions(new File(getDataFolder(), "extensions"));

        if (pm != null) {
            pm.stopAllListeners();
        }

        pm = new PunishmentListeners(getLogger());

        pm.addNew("LibertyBans", "libertybans", () -> new LibertyBansListener(this), true, "space.arim.libertybans.api.LibertyBans");
        pm.addNew("LiteBans"   , "litebans"   , () -> new LiteBansListener(this)   , true, "litebans.api.Events");

        for (Extension ext : announcer.getExtensions()) {
            pm.addNew(ext.getName(), ext.getKey(), ext.getInstanceSupplier(this), ext.isPunishmentManager(), ext.getRequiredClass());
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
    public File getFile() {
        return ReflectUtils.getJarFile(BanAnnouncerVelocity.class);
    }

    @Override
    public BanAnnouncer getAnnouncer() {
        return announcer;
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
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
        Plugin info = BanAnnouncerVelocity.class.getAnnotation(Plugin.class);
        if (info == null) {
            return "unknown";
        }
        return info.version();
    }
}
