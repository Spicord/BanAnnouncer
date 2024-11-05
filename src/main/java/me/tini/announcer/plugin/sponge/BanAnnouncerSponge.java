package me.tini.announcer.plugin.sponge;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.spicord.Spicord;
import org.spicord.SpicordLoader;
import org.spicord.reflect.ReflectUtils;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.google.inject.Inject;

import me.tini.announcer.BanAnnouncer;
import me.tini.announcer.BanAnnouncerPlugin;
import me.tini.announcer.ReloadCommand;
import me.tini.announcer.addon.BanAnnouncerAddon;
import me.tini.announcer.config.Config;
import me.tini.announcer.extension.impl.libertybans.LibertyBansExtension;
import me.tini.announcer.extension.impl.spongevanilla.SpongeVanillaExtension;

@Plugin("banannouncer")
public class BanAnnouncerSponge implements BanAnnouncerPlugin {

    private final Path dataFolder;
    private final Game game;
    private final PluginContainer pluginContainer;

    private BanAnnouncer announcer;

    @Inject
    public BanAnnouncerSponge(
        @ConfigDir(sharedRoot = false) Path dataFolder,
        Game game,
        PluginContainer pluginContainer
    ) {
        this.dataFolder = dataFolder;
        this.game = game;
        this.pluginContainer = pluginContainer;
    }

    public Game getGame() {
        return game;
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public void onEnable() {
        SpicordLoader.addStartupListener(this::onSpicordLoad);
    }

    private void onSpicordLoad(Spicord spicord) {
        Config config = new Config(this);

        new ReloadCommand().register(this);

        announcer = new BanAnnouncer(config, spicord, this);

        announcer.loadExtensions(new File(getDataFolder(), "extensions"));

        announcer.registerExtension("LibertyBans", "libertybans", () -> new LibertyBansExtension(this)  , "space.arim.libertybans.api.LibertyBans");
        announcer.registerExtension("Sponge"     , "sponge"     , () -> new SpongeVanillaExtension(this), "org.spongepowered.api.Game");

        announcer.enableExtensions();

        spicord.getAddonManager().registerAddon(new BanAnnouncerAddon(this), this);
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
        return Logger.getLogger("banannouncer");
    }

    @Override
    public File getDataFolder() {
        return dataFolder.toFile();
    }
}
