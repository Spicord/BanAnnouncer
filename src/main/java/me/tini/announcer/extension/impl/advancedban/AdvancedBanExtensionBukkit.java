package me.tini.announcer.extension.impl.advancedban;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.plugin.bukkit.BanAnnouncerBukkit;

public class AdvancedBanExtensionBukkit extends AbstractExtension {

    private PunishmentListener listener;

    public AdvancedBanExtensionBukkit(BanAnnouncerBukkit plugin) {
        this.listener = new AdvancedBanListenerBukkit(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
