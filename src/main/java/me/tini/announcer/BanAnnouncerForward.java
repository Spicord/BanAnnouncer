package me.tini.announcer;

import com.google.gson.Gson;

import me.tini.announcer.config.Config;
import me.tini.announcer.embed.Embed;

public final class BanAnnouncerForward extends BanAnnouncer {

    private static final String CHANNEL = "banannouncer:punishment";
    private final Gson gson = new Gson();
    private IMessenger messenger;

    public BanAnnouncerForward(Config config, BanAnnouncerPlugin plugin) {
        super(config, plugin);

        this.messenger = (IMessenger) plugin;
        this.messenger.registerOutgoingChannel(CHANNEL);
    }

    @Override
    public void initialize() {
        super.enabled = true;
    }

    @Override
    public void handlePunishment(PunishmentInfo punishment, PunishmentListener listener) {
        // Forward punishment to proxy server

        final byte[] payload = gson.toJson(punishment).getBytes();

        messenger.sendMessage(CHANNEL, payload);
    }

    @Override
    public void sendDiscordMessage(Embed message) {
        // This mode does not sends messages, it forwards them to the proxy server.
    }
}
