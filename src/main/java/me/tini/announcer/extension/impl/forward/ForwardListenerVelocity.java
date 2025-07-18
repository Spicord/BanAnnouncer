package me.tini.announcer.extension.impl.forward;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import me.tini.announcer.ForwardInfo;
import me.tini.announcer.PunishmentInfo;
import me.tini.announcer.plugin.velocity.BanAnnouncerVelocity;
import me.tini.announcer.plugin.velocity.VelocityPunishmentListener;

public class ForwardListenerVelocity extends VelocityPunishmentListener {

    private final ChannelIdentifier CHANNEL;

    public ForwardListenerVelocity(BanAnnouncerVelocity plugin) {
        super(plugin);

        String[] split = ForwardInfo.CHANNEL.split(":", 2);
        String namespace = split[0];
        String name = split[1];

        CHANNEL = MinecraftChannelIdentifier.create(namespace, name);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!(event.getSource() instanceof ServerConnection)) {
            return;
        }
        if (!CHANNEL.equals(event.getIdentifier())) {
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
        getPlugin().getProxyServer().getChannelRegistrar().register(CHANNEL);
        super.register();
    }

    @Override
    public void unregister() {
        getPlugin().getProxyServer().getChannelRegistrar().unregister(CHANNEL);
        super.unregister();
    }
}
