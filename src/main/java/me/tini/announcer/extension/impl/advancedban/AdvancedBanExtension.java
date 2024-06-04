package me.tini.announcer.extension.impl.advancedban;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.bukkit.BanAnnouncerBukkit;
import me.tini.announcer.bungee.BanAnnouncerBungee;
import me.tini.announcer.extension.AbstractExtension;

public class AdvancedBanExtension extends AbstractExtension {

    private PunishmentListener listener;

    public AdvancedBanExtension(BanAnnouncerBukkit plugin) {
        this.listener = new AdvancedBanListenerBukkit(plugin);
    }

    public AdvancedBanExtension(BanAnnouncerBungee plugin) {
        this.listener = new AdvancedBanListenerBungee(plugin);
    }

    @Override
    public PunishmentListener getPunishmentListener() {
        return listener;
    }
}
