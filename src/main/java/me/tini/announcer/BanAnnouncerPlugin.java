package me.tini.announcer;

import org.spicord.plugin.PluginInterface;

public interface BanAnnouncerPlugin extends PluginInterface {

    BanAnnouncer getAnnouncer();

    String getVersion();

    default void log(String msg, Object... args) {
        getLogger().info(String.format(msg, args));
    }

    default void warn(String msg, Object... args) {
        getLogger().warning(String.format(msg, args));
    }
}
