package me.tini.announcer.plugin.velocity;

import java.io.File;
import java.util.logging.Logger;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;

import me.tini.announcer.BanAnnouncer;
import me.tini.announcer.BanAnnouncerPlugin;
import me.tini.announcer.ReloadCommand;
import me.tini.announcer.config.Config;
import me.tini.announcer.extension.impl.libertybans.LibertyBansExtension;
import me.tini.announcer.extension.impl.litebans.LiteBansExtension;
import me.tini.announcer.utils.ReflectUtils;
import me.tini.announcer.utils.SLF4JWrapper;

@Plugin(
    id = "ban_announcer",
    name = "BanAnnouncer",
    version = "2.9.0",
    authors = { "Tini" },
    dependencies = {
        @Dependency(id = "spicord", optional = true),
        @Dependency(id = "libertybans", optional = true),
        @Dependency(id = "litebans", optional = true)
    }
)
public class BanAnnouncerVelocity implements BanAnnouncerPlugin {

    private BanAnnouncer announcer;
    private Config config;
    private Logger logger;
    private File dataFolder;

    public BanAnnouncerVelocity() {
        this.logger = new SLF4JWrapper("BanAnnouncer");
        this.dataFolder = new File("plugins/ban_announcer");
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        config = new Config(this);

        new ReloadCommand().register(this);

        announcer = BanAnnouncer.build(this, config);

        announcer.loadExtensions(new File(getDataFolder(), "extensions"));

        announcer.registerExtension("LiteBans"   , "litebans"   , () -> new LiteBansExtension(this)   , "litebans.api.Events");
        announcer.registerExtension("LibertyBans", "libertybans", () -> new LibertyBansExtension(this), "space.arim.libertybans.api.LibertyBans");

        announcer.enableExtensions();

        announcer.initialize();
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

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }
}
