package me.tini.announcer;

import me.tini.announcer.config.Config;
import me.tini.announcer.embed.Embed;

public final class BanAnnouncerForward extends BanAnnouncer {

    private IMessenger messenger;

    public BanAnnouncerForward(Config config, BanAnnouncerPlugin plugin) {
        super(config, plugin);

        this.messenger = (IMessenger) plugin;
        this.messenger.registerOutgoingChannel(ForwardInfo.CHANNEL);
    }

    @Override
    public void initialize() {
        super.enabled = true;
    }

    @Override
    public void handlePunishment(PunishmentInfo punishment, PunishmentListener listener) {
        // Forward punishment to proxy server

        final byte[] payload = ForwardInfo.GSON.toJson(punishment).getBytes();

        final boolean success = messenger.sendMessage(ForwardInfo.CHANNEL, payload);

        if (!success) {
            plugin.warn("Punishment not forwarded because there are no players connected to the server.");
        }
    }

    @Override
    public void sendDiscordMessage(Embed message) {
        // This mode does not sends messages, it forwards them to the proxy server.
    }
}
