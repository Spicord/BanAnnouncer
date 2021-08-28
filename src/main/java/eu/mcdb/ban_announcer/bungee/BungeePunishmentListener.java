package eu.mcdb.ban_announcer.bungee;

import eu.mcdb.ban_announcer.PunishmentListener;
import net.md_5.bungee.api.plugin.Listener;

public class BungeePunishmentListener extends PunishmentListener implements Listener {

    private final BanAnnouncerBungee plugin;

    public BungeePunishmentListener(BanAnnouncerBungee plugin) {
        super(plugin.getAnnouncer());
        this.plugin = plugin;
    }

    public BanAnnouncerBungee getPlugin() {
        return plugin;
    }

    @Override
    public void register() {
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @Override
    public void unregister() {
        plugin.getProxy().getPluginManager().unregisterListener(this);
    }
}
