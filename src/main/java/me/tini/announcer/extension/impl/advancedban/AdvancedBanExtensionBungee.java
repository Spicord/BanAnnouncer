package me.tini.announcer.extension.impl.advancedban;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.plugin.bungee.BanAnnouncerBungee;

public class AdvancedBanExtensionBungee extends AbstractExtension {

    private PunishmentListener listener;

    public AdvancedBanExtensionBungee(BanAnnouncerBungee plugin) {
        this.listener = new AdvancedBanListenerBungee(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
