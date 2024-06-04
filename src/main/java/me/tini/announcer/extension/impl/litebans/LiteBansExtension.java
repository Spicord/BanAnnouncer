package me.tini.announcer.extension.impl.litebans;

import me.tini.announcer.BanAnnouncerPlugin;
import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;

public final class LiteBansExtension extends AbstractExtension {

    private LiteBansListener listener;

    public LiteBansExtension(BanAnnouncerPlugin plugin) {
        this.listener = new LiteBansListener(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
