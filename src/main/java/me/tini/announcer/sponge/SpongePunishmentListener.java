package me.tini.announcer.sponge;

import me.tini.announcer.PunishmentListener;

public class SpongePunishmentListener extends PunishmentListener {

    private final BanAnnouncerSponge plugin;

    public SpongePunishmentListener(BanAnnouncerSponge plugin) {
        super(plugin.getAnnouncer());
        this.plugin = plugin;
    }

    public BanAnnouncerSponge getPlugin() {
        return plugin;
    }

    @Override
    public void register() {
        plugin.getGame().eventManager().registerListeners(plugin.getPluginContainer(), this);
    }

    @Override
    public void unregister() {
        plugin.getGame().eventManager().unregisterListeners(this);
    }
}
