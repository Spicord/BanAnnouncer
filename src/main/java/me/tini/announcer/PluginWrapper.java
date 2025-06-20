package me.tini.announcer;

import java.io.File;
import java.util.logging.Logger;

import org.spicord.plugin.PluginInterface;

public class PluginWrapper implements PluginInterface {

    private final BanAnnouncerPlugin plugin;

    public PluginWrapper(BanAnnouncerPlugin plugin) {
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
