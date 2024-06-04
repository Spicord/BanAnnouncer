package me.tini.announcer.plugin.velocity;

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
import me.tini.announcer.ReloadCommand;
import me.tini.announcer.addon.BanAnnouncerAddon;
import me.tini.announcer.config.Config;
import me.tini.announcer.extension.impl.libertybans.LibertyBansExtension;
import me.tini.announcer.extension.impl.litebans.LiteBansExtension;

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

        announcer = new BanAnnouncer(config, spicord, this);

        announcer.loadExtensions(new File(getDataFolder(), "extensions"));

        announcer.registerExtension("LiteBans"   , "litebans"   , () -> new LiteBansExtension(this)   , "litebans.api.Events");
        announcer.registerExtension("LibertyBans", "libertybans", () -> new LibertyBansExtension(this), "space.arim.libertybans.api.LibertyBans");

        announcer.enableExtensions();

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
