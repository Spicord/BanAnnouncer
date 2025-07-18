package me.tini.announcer.plugin.velocity;

import me.tini.announcer.PunishmentListener;

public class VelocityPunishmentListener extends PunishmentListener {

    private final BanAnnouncerVelocity plugin;

    public VelocityPunishmentListener(BanAnnouncerVelocity plugin) {
        super(plugin.getAnnouncer());
        this.plugin = plugin;
    }

    public BanAnnouncerVelocity getPlugin() {
        return plugin;
    }

    @Override
    public void register() {
        plugin.getProxyServer().getEventManager().register(plugin, this);
    }

    @Override
    public void unregister() {
        plugin.getProxyServer().getEventManager().unregisterListener(plugin, this);
    }
}
