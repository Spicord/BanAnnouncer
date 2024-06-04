package me.tini.announcer.extension.impl.betterjails;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.plugin.bukkit.BanAnnouncerBukkit;

public class BetterJailsExtension extends AbstractExtension {

    private BetterJailsListener listener;

    public BetterJailsExtension(BanAnnouncerBukkit plugin) {
        this.listener = new BetterJailsListener(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
