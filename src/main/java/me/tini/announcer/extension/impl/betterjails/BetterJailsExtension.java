package me.tini.announcer.extension.impl.betterjails;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.bukkit.BanAnnouncerBukkit;
import me.tini.announcer.extension.AbstractExtension;

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
