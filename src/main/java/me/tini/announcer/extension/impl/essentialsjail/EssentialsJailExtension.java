package me.tini.announcer.extension.impl.essentialsjail;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.plugin.bukkit.BanAnnouncerBukkit;

public class EssentialsJailExtension extends AbstractExtension {

    private EssentialsJailListener listener;

    public EssentialsJailExtension(BanAnnouncerBukkit plugin) {
        this.listener = new EssentialsJailListener(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
