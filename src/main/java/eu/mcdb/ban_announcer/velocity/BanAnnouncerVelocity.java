package eu.mcdb.ban_announcer.velocity;

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

import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.ban_announcer.BanAnnouncerPlugin;
import eu.mcdb.ban_announcer.PunishmentListeners;
import eu.mcdb.ban_announcer.config.Config;
import eu.mcdb.ban_announcer.extension.Extension;
import eu.mcdb.ban_announcer.listener.LibertyBansListener;

@Plugin(
    id = "ban_announcer",
    name = "BanAnnouncer",
    version = "2.5.0",
    authors = { "Sheidy" },
    dependencies = {
        @Dependency(id = "spicord", optional = false),
        @Dependency(id = "libertybans", optional = true)
    }
)
public class BanAnnouncerVelocity extends VelocityPlugin implements BanAnnouncerPlugin {

    private BanAnnouncer announcer;
    private PunishmentListeners pm;
    private Config config;

    @Inject
    public BanAnnouncerVelocity(ProxyServer proxyServer) {
        super(proxyServer);
        SpicordLoader.addStartupListener(this::onSpicordLoad);
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
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

    private void onSpicordLoad(Spicord spicord) {
        config = new Config(this);

        this.announcer = new BanAnnouncer(config, spicord);

        this.announcer.loadExtensions(new File(getDataFolder(), "extensions"));

        pm = new PunishmentListeners(getLogger());

        pm.addNew("LibertyBans", "libertybans", () -> new LibertyBansListener(this), true, "space.arim.libertybans.api.LibertyBans");

        for (Extension ext : announcer.getExtensions()) {
            pm.addNew(ext.getName(), ext.getKey(), ext.getInstanceSupplier(this), ext.isPunishmentManager(), ext.getRequiredClass());
        }
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
}
