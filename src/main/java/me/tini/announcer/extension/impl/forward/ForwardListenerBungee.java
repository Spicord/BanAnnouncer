package me.tini.announcer.extension.impl.forward;

import com.google.gson.Gson;

import me.tini.announcer.PunishmentInfo;
import me.tini.announcer.plugin.bungee.BanAnnouncerBungee;
import me.tini.announcer.plugin.bungee.BungeePunishmentListener;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

public class ForwardListenerBungee extends BungeePunishmentListener {

    private static final String CHANNEL = "banannouncer:punishment";
    private final Gson gson = new Gson();

    public ForwardListenerBungee(BanAnnouncerBungee plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!(event.getSender() instanceof Server)) {
            return;
        }
        if (!CHANNEL.equals(event.getTag())) {
            return;
        }

        PunishmentInfo punishment = gson.fromJson(
            new String(event.getData()),
            PunishmentInfo.class
        );

        handlePunishment(punishment);
    }

    @Override
    public void register() {
        getPlugin().getProxy().registerChannel(CHANNEL);
        super.register();
    }

    @Override
    public void unregister() {
        getPlugin().getProxy().unregisterChannel(CHANNEL);
        super.unregister();
    }
}
