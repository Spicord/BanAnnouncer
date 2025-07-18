package me.tini.announcer.extension.impl.forward;

import me.tini.announcer.ForwardInfo;
import me.tini.announcer.PunishmentInfo;
import me.tini.announcer.plugin.bungee.BanAnnouncerBungee;
import me.tini.announcer.plugin.bungee.BungeePunishmentListener;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

public class ForwardListenerBungee extends BungeePunishmentListener {

    public ForwardListenerBungee(BanAnnouncerBungee plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!(event.getSender() instanceof Server)) {
            return;
        }
        if (!ForwardInfo.CHANNEL.equals(event.getTag())) {
            return;
        }

        PunishmentInfo punishment = ForwardInfo.GSON.fromJson(
            new String(event.getData()),
            PunishmentInfo.class
        );

        handlePunishment(punishment);
    }

    @Override
    public void register() {
        getPlugin().getProxy().registerChannel(ForwardInfo.CHANNEL);
        super.register();
    }

    @Override
    public void unregister() {
        getPlugin().getProxy().unregisterChannel(ForwardInfo.CHANNEL);
        super.unregister();
    }
}
