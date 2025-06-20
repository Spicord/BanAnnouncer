package me.tini.announcer;

import org.spicord.Spicord;
import org.spicord.SpicordLoader;
import org.spicord.bot.DiscordBot;
import org.spicord.embed.EmbedSender;

import me.tini.announcer.addon.BanAnnouncerAddon;
import me.tini.announcer.addon.Helper;
import me.tini.announcer.config.Config;
import me.tini.announcer.utils.Embed;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public final class BanAnnouncerSpicord extends BanAnnouncer {

    private DiscordBot bot;

    public BanAnnouncerSpicord(Config config, BanAnnouncerPlugin plugin) {
        super(config, plugin);
    }

    public void initialize() {
        SpicordLoader.addStartupListener(this::onSpicordLoad);
    }

    private void onSpicordLoad(Spicord spicord) {
        spicord.getAddonManager().registerAddon(
            new BanAnnouncerAddon(plugin),
            new PluginWrapper(plugin)
        );
        enabled = true;
    }

    @Override
    public void sendDiscordMessage(Embed message) {
        if (message == null) {
            logger.warning("(message is null, ignoring it)");
            return;
        }

        if (bot == null || bot.getJda() == null) {
            logger.warning("BanAnnouncer does not have access to an active bot.");
            return;
        }

        JDA jda = bot.getJda();

        long channelId = config.getChannelToAnnounce();

        GuildChannel channel = jda.getGuildChannelById(channelId);

        if (channel == null || !(channel instanceof GuildMessageChannel)) {
            logger.severe("Cannot find the channel with id '" + channelId + "'. The message was not sent.");
            return;
        } else {
            EmbedSender.prepare((GuildMessageChannel) channel, Helper.toSpicordEmbed(message)).queue(success -> {
                logger.info("The punishment message was sent.");
            }, fail -> {
                logger.warning("Couldn't send the punishment message: " + fail.getMessage());
            });
        }
    }

    public void setBot(DiscordBot bot) {
        this.bot = bot;
    }

    public void removeBot(DiscordBot bot) {
        this.bot = null;
    }

    public void disable() {
        super.disable();
        bot = null;
    }
}
