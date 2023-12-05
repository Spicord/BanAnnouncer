/*
 * Copyright (C) 2019  OopsieWoopsie
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.tini.announcer.addon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.spicord.api.addon.SimpleAddon;
import org.spicord.bot.DiscordBot;
import org.spicord.bot.command.SlashCommand;
import org.spicord.embed.Embed;

import me.tini.announcer.BanAnnouncer;
import me.tini.announcer.BanAnnouncerPlugin;
import me.tini.announcer.PunishmentAction;
import me.tini.announcer.config.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public final class BanAnnouncerAddon extends SimpleAddon {

    private BanAnnouncer announcer;

    public BanAnnouncerAddon(BanAnnouncerPlugin plugin) {
        super("BanAnnouncer", "ban_announcer", "Tini", plugin.getVersion());
        this.announcer = plugin.getAnnouncer();
    }

    @Override
    public void onReady(DiscordBot bot) {
        long channelId = announcer.getConfig().getChannelToAnnounce();

        GuildChannel channel = bot.getJda().getGuildChannelById(channelId);

        if (channel == null) {
            getLogger().severe(String.format(
                "The channel with id '%d' was not found, please fix it and reload the config!",
                channelId
            ));
        }

        if (!announcer.getConfig().isUseDiscordCommand()) {
            return;
        }

        if (channel == null) {
            getLogger().severe(String.format("The channel with id '%d' was not found, the Discord command could not be registered.", channelId));
            return;
        }

        Guild guild = channel.getGuild();

        SlashCommand main = new SlashCommand("banannouncer", "Main BanAnnouncer command")
            .setDefaultPermissions(Permission.BAN_MEMBERS)
            .addSubcommand(
                new SlashCommand("test", "Test an embed file")
                    .addOption(OptionType.STRING, "embed", "The embed name", true, true)
                    .setExecutor(event -> {
                        OptionMapping nameOption = event.getOption("embed");

                        if (nameOption == null) {
                            return;
                        }

                        String embedName = nameOption.getAsString();

                        final PunishmentAction.Type type;
                        try {
                            type = PunishmentAction.Type.valueOf(embedName.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            event.reply("The specified embed does not exists")
                                .setEphemeral(true)
                                .queue();
                            return;
                        }

                        PunishmentAction action = new PunishmentAction(type);
                        action.setId("0");
                        action.setPlayer("Wumpus");
                        action.setPlayerId(new UUID(0, 0).toString());
                        action.setOperator(event.getUser().getName());
                        action.setReason("Eating too many cookies");

                        action.setDuration("forever");
                        action.setJail("jail1");
                        action.setPermanent(true);

                        Embed embed = announcer.buildEmbed(action);

                        if (embed == null) {
                            event.reply("The specified embed is disabled in your server").setEphemeral(true).queue();
                            return;
                        }

                        if (embed.hasEmbedData() && embed.hasContent()) {
                            event.replyEmbeds(embed.toJdaEmbed())
                                .addContent(embed.getContent())
                                .setEphemeral(true)
                                .queue();
                        } else if (embed.hasEmbedData()) {
                            event.replyEmbeds(embed.toJdaEmbed())
                                .setEphemeral(true)
                                .queue();
                        } else if (embed.hasContent()) {
                            event.reply(embed.getContent())
                                .setEphemeral(true)
                                .queue();
                        } else {
                            event.reply("That embed file appears to be empty")
                                .setEphemeral(true)
                                .queue();
                        }
                    })
                    .setCompleter(event -> {
                        List<String> list = new ArrayList<>();
                        for (PunishmentAction.Type type : PunishmentAction.Type.values()) {
                            list.add(type.name().toLowerCase());
                        }
                        event.replyChoiceStrings(list).queue();
                    })
            )
            .addSubcommand(
                new SlashCommand("reload", "Reloads the BanAnnouncer configuration")
                    .setExecutor(event -> {
                        Config.getInstance().reload();

                        event.reply("Reloaded the configuration").setEphemeral(true).queue();
                    })
            )
        ;

        bot.registerCommand(main, guild);
    }

    @Override
    public void onLoad(DiscordBot bot) {
        announcer.setBot(bot);
    }

    @Override
    public void onShutdown(DiscordBot bot) {
        announcer.removeBot(bot);
    }
}
