package me.tini.announcer.spicord;

import me.tini.announcer.embed.Embed;

public class Conversions {

    public static org.spicord.embed.Embed toSpicordEmbed(Embed baEmbed) {
        return org.spicord.embed.Embed.fromJson(baEmbed.toJson());
    }
}
