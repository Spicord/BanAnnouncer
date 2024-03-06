package me.tini.announcer;

import org.spicord.plugin.PluginInterface;

public interface BanAnnouncerPlugin extends PluginInterface {

    BanAnnouncer getAnnouncer();

    PunishmentListeners getPunishmentListeners();

    String getVersion();

}
