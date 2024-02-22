package me.tini.announcer.sponge;

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
import me.tini.announcer.PunishmentListeners;
import me.tini.announcer.ReloadCommand;
import me.tini.announcer.addon.BanAnnouncerAddon;
import me.tini.announcer.config.Config;
import me.tini.announcer.extension.Extension;
import me.tini.announcer.listener.LibertyBansListener;
import me.tini.announcer.sponge.listener.VanillaListener;

@Plugin("banannouncer")
public class BanAnnouncerSponge implements BanAnnouncerPlugin {

    private final Path dataFolder;
    private final Game game;
    private final PluginContainer pluginContainer;

    private BanAnnouncer announcer;
    private PunishmentListeners pm;

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

        this.announcer = new BanAnnouncer(config, spicord);

        this.announcer.loadExtensions(new File(getDataFolder(), "extensions"));

        if (pm != null) {
            pm.stopAllListeners();
        }

        pm = new PunishmentListeners(getLogger());

        // General punishments
        pm.addNew(
            "LibertyBans",
            "libertybans",
            () -> new LibertyBansListener(this),
            true,
            "space.arim.libertybans.api.LibertyBans"
        );
        pm.addNew(
            "Sponge",
            "sponge",
            () -> new VanillaListener(this),
            true,
            "org.spongepowered.api.Game"
        );

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
