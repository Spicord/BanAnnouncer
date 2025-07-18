package me.tini.announcer.extension.impl.forward;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.plugin.bungee.BanAnnouncerBungee;

public class ForwardExtensionBungee extends AbstractExtension {

    private ForwardListenerBungee listener;

    public ForwardExtensionBungee(BanAnnouncerBungee plugin) {
        listener = new ForwardListenerBungee(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
