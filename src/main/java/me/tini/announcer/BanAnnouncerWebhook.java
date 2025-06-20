package me.tini.announcer;

import java.io.IOException;

import me.tini.announcer.config.Config;
import me.tini.announcer.embed.Embed;

public final class BanAnnouncerWebhook extends BanAnnouncer {

    public BanAnnouncerWebhook(Config config, BanAnnouncerPlugin plugin) {
        super(config, plugin);
    }

    @Override
    public void initialize() {
        enabled = true;
    }

    @Override
    public void sendDiscordMessage(Embed message) {
        if (message == null) {
            logger.warning("(message is null, ignoring it)");
            return;
        }

        String webHookUrl = config.getWebhookUrl();

        try {
            message.toWebhook().sendTo(webHookUrl);
        } catch (IOException e) {
            new RuntimeException("Failed to send embed message", e).printStackTrace();
        }
    }
}
