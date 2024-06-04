package me.tini.announcer.extension.impl.advancedban;

import me.tini.announcer.PunishmentListener;
import me.tini.announcer.extension.AbstractExtension;
import me.tini.announcer.plugin.bukkit.BanAnnouncerBukkit;
import me.tini.announcer.plugin.bungee.BanAnnouncerBungee;

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
