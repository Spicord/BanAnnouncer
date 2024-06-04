package me.tini.announcer.extension.impl.maxbans;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.plugin.bukkit.BanAnnouncerBukkit;

public class MaxBansExtension extends AbstractExtension {

    private MaxBansListener listener;

    public MaxBansExtension(BanAnnouncerBukkit plugin) {
        this.listener = new MaxBansListener(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
