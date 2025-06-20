package me.tini.announcer.addon;

import me.tini.announcer.utils.Embed;

public class Helper {

    public static org.spicord.embed.Embed toSpicordEmbed(Embed baEmbed) {
        return org.spicord.embed.Embed.fromJson(baEmbed.toJson());
    }
}
