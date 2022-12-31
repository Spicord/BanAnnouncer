package me.tini.announcer.bukkit;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import me.tini.announcer.PunishmentListener;

public class BukkitPunishmentListener extends PunishmentListener implements Listener {

    private final BanAnnouncerBukkit plugin;

    public BukkitPunishmentListener(BanAnnouncerBukkit plugin) {
        super(plugin.getAnnouncer());
        this.plugin = plugin;
    }

    public BanAnnouncerBukkit getPlugin() {
        return plugin;
    }

    @Override
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
