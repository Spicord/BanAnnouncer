package me.tini.announcer.plugin.sponge;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.google.inject.Inject;

import me.tini.announcer.BanAnnouncer;
import me.tini.announcer.BanAnnouncerPlugin;
import me.tini.announcer.ReloadCommand;
import me.tini.announcer.config.Config;
import me.tini.announcer.extension.impl.libertybans.LibertyBansExtension;
import me.tini.announcer.extension.impl.spongevanilla.SpongeVanillaExtension;
import me.tini.announcer.utils.Log4JWrapper;
import me.tini.announcer.utils.ReflectUtils;
import me.tini.command.sponge.ISpongePlugin;

@Plugin("banannouncer")
public class BanAnnouncerSponge implements BanAnnouncerPlugin, ISpongePlugin {

    private final Path dataFolder;
    private final Game game;
    private final PluginContainer pluginContainer;

    private BanAnnouncer announcer;
    private Logger logger;

    @Inject
    public BanAnnouncerSponge(
        @ConfigDir(sharedRoot = false) Path dataFolder,
        Game game,
        PluginContainer pluginContainer,
        org.apache.logging.log4j.Logger log
    ) {
        this.dataFolder = dataFolder;
        this.game = game;
        this.pluginContainer = pluginContainer;

        this.logger = new Log4JWrapper(log);
    }

    public Game getGame() {
        return game;
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @Listener
    public void onEnable(ConstructPluginEvent event) {
        Config config = new Config(this);

        registerCommand("banannouncer-reload", new ReloadCommand());

        announcer = BanAnnouncer.build(this, config);

        announcer.loadExtensions(new File(getDataFolder(), "extensions"));

        announcer.registerExtension("LibertyBans", "libertybans", () -> new LibertyBansExtension(this)  , "space.arim.libertybans.api.LibertyBans");
        announcer.registerExtension("Sponge"     , "sponge"     , () -> new SpongeVanillaExtension(this), "org.spongepowered.api.Game");

        announcer.enableExtensions();

        announcer.initialize();
    }

    public void onDisable() {
        if (announcer != null) {
            announcer.disable();
            announcer = null;
        }
    }

    @Override
    public BanAnnouncer getAnnouncer() {
        return announcer;
    }

    @Override
    public String getVersion() {
        ArtifactVersion version = pluginContainer.metadata().version();

        return String.format(
            "%d.%d.%d",
            version.getMajorVersion(),
            version.getMinorVersion(),
            version.getIncrementalVersion()
        );
    }

    @Override
    public File getFile() {
        return ReflectUtils.getJarFile(BanAnnouncerSponge.class);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public File getDataFolder() {
        return dataFolder.toFile();
    }
}
