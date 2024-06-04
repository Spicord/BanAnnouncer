package me.tini.announcer.extension.impl.libertybans;

import me.tini.announcer.BanAnnouncerPlugin;
import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;

public class LibertyBansExtension extends AbstractExtension {

    private LibertyBansListener listener;

    public LibertyBansExtension(BanAnnouncerPlugin plugin) {
        this.listener = new LibertyBansListener(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
