package me.tini.announcer.extension.impl.spongevanilla;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.sponge.BanAnnouncerSponge;

public class SpongeVanillaExtension extends AbstractExtension {

    private SpongeVanillaListener listener;

    public SpongeVanillaExtension(BanAnnouncerSponge plugin) {
        this.listener = new SpongeVanillaListener(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
