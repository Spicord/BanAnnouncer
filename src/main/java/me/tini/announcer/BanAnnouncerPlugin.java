package me.tini.announcer;

import org.spicord.plugin.PluginInterface;

public interface BanAnnouncerPlugin extends PluginInterface {

    public BanAnnouncer getAnnouncer();

    public String getVersion();

}
