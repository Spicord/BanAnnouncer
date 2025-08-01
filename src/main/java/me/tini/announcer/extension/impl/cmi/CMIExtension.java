package me.tini.announcer.extension.impl.cmi;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.plugin.bukkit.BanAnnouncerBukkit;

public class CMIExtension extends AbstractExtension {

    private PunishmentListener listener;

    public CMIExtension(BanAnnouncerBukkit plugin) {
        this.listener = new CMIEventListener(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
