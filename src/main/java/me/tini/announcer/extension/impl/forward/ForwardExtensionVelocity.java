package me.tini.announcer.extension.impl.forward;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.plugin.velocity.BanAnnouncerVelocity;

public class ForwardExtensionVelocity extends AbstractExtension {

    private ForwardListenerVelocity listener;

    public ForwardExtensionVelocity(BanAnnouncerVelocity plugin) {
        listener = new ForwardListenerVelocity(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
