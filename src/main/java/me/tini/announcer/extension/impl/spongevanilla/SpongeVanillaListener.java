package me.tini.announcer.extension.impl.spongevanilla;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.player.KickPlayerEvent;
import org.spongepowered.api.event.network.BanIpEvent;
import org.spongepowered.api.event.network.PardonIpEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.event.user.PardonUserEvent;

import me.tini.announcer.PunishmentInfo;
import me.tini.announcer.PunishmentInfo.Type;
import me.tini.announcer.plugin.sponge.BanAnnouncerSponge;
import me.tini.announcer.plugin.sponge.SpongePunishmentListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class SpongeVanillaListener extends SpongePunishmentListener {

    public SpongeVanillaListener(BanAnnouncerSponge plugin) {
        super(plugin);
    }

    @Listener
    public void onKickPlayer(KickPlayerEvent event) {
        Player player = event.player();
        Component messageComponent = event.message();

        PunishmentInfo pun = new PunishmentInfo(Type.KICK);

        pun.setPlayer(player.name());
        pun.setPlayerId(player.uniqueId().toString());

        Optional<Player> operator = event.cause().first(Player.class);

        if (operator.isPresent()) {
            pun.setOperator(operator.get().name());
        } else {
            pun.setOperator("Console");
        }

        if (messageComponent != null) {
            String reason = PlainTextComponentSerializer.plainText().serialize(messageComponent);

            pun.setReason(reason);
        }

        super.handlePunishment(pun);
    }

    @Listener
    public void onBanUser(BanUserEvent event) {
        User player = event.user();

        PunishmentInfo pun = new PunishmentInfo(Type.BAN);

        pun.setPlayer(player.name());
        pun.setPlayerId(player.uniqueId().toString());

        Optional<Player> operator = event.cause().first(Player.class);

        if (operator.isPresent()) {
            pun.setOperator(operator.get().name());
        } else {
            pun.setOperator("Console");
        }

        pun.setPermanent(true);

        super.handlePunishment(pun);
    }

    @Listener
    public void onBanUser(PardonUserEvent event) {
        User player = event.user();

        PunishmentInfo pun = new PunishmentInfo(Type.UNBAN);

        pun.setPlayer(player.name());
        pun.setPlayerId(player.uniqueId().toString());

        Optional<Player> operator = event.cause().first(Player.class);

        if (operator.isPresent()) {
            pun.setOperator(operator.get().name());
        } else {
            pun.setOperator("Console");
        }

        super.handlePunishment(pun);
    }

    @Listener
    public void onBanIp(BanIpEvent event) {
        InetAddress address = event.ban().address();

        PunishmentInfo pun = new PunishmentInfo(Type.BANIP);

        pun.setPlayer(censorIp(address));

        Optional<Player> operator = event.cause().first(Player.class);

        if (operator.isPresent()) {
            pun.setOperator(operator.get().name());
        } else {
            pun.setOperator("Console");
        }

        pun.setPermanent(true);

        super.handlePunishment(pun);
    }

    @Listener
    public void onPardonIp(PardonIpEvent event) {
        InetAddress address = event.ban().address();

        PunishmentInfo pun = new PunishmentInfo(Type.UNBANIP);

        pun.setPlayer(censorIp(address));

        Optional<Player> operator = event.cause().first(Player.class);

        if (operator.isPresent()) {
            pun.setOperator(operator.get().name());
        } else {
            pun.setOperator("Console");
        }

        super.handlePunishment(pun);
    }

    private static String censorIp(InetAddress address) {
        String ip = address.getHostAddress();

        if (address instanceof Inet4Address) {
            return ip.substring(0, ip.lastIndexOf('.') + 1) + "***";
        }
        if (address instanceof Inet6Address) {
            return ip.substring(0, ip.length() - 3) + "***";
        }

        return ip; // :shrug:
    }
}
