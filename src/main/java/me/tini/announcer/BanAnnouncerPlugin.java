package me.tini.announcer;

import java.io.File;
import java.util.logging.Logger;

public interface BanAnnouncerPlugin {

    BanAnnouncer getAnnouncer();

    String getVersion();

    File getFile();

    File getDataFolder();

    Logger getLogger();

    default void log(String msg, Object... args) {
        getLogger().info(String.format(msg, args));
    }

    default void warn(String msg, Object... args) {
        getLogger().warning(String.format(msg, args));
    }
}
