package me.tini.announcer.spicord;

import java.io.File;
import java.util.logging.Logger;

import org.spicord.plugin.PluginInterface;

import me.tini.announcer.BanAnnouncerPlugin;

public class SpicordPluginWrapper implements PluginInterface {

    private final BanAnnouncerPlugin plugin;

    public SpicordPluginWrapper(BanAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public File getFile() {
        return plugin.getFile();
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }
}
