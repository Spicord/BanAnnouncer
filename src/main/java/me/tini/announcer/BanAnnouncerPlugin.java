package me.tini.announcer;

import java.io.File;
import java.util.logging.Logger;

public interface BanAnnouncerPlugin {

    public Logger getLogger();

    public File getDataFolder();

    public File getFile();

    public BanAnnouncer getAnnouncer();

    public String getVersion();
}
