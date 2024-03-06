package me.tini.announcer.bukkit.listener;

import com.github.fefo.betterjails.api.BetterJails;
import com.github.fefo.betterjails.api.event.prisoner.PlayerImprisonEvent;
import com.github.fefo.betterjails.api.event.prisoner.PrisonerReleaseEvent;
import com.github.fefo.betterjails.api.model.prisoner.Prisoner;

import me.tini.announcer.PunishmentInfo;
import me.tini.announcer.PunishmentInfo.Type;
import me.tini.announcer.bukkit.BanAnnouncerBukkit;
import me.tini.announcer.bukkit.BukkitPunishmentListener;
import me.tini.announcer.utils.TimeUtils;

public class BetterJailsListener extends BukkitPunishmentListener {

    private final BetterJails betterJails;

    public BetterJailsListener(BanAnnouncerBukkit plugin) {
        super(plugin);
        this.betterJails = plugin.getServer().getServicesManager().load(BetterJails.class);
    }

    @Override
    public void register() {
        betterJails.getEventBus().subscribe(getPlugin(), PlayerImprisonEvent.class, this::onPlayerImprison);
        betterJails.getEventBus().subscribe(getPlugin(), PrisonerReleaseEvent.class, this::onPrisonerRelease);
    }

    @Override
    public void unregister() {
        betterJails.getEventBus().unsubscribe(getPlugin());
    }

    private void onPlayerImprison(PlayerImprisonEvent event) {
        handle(event.prisoner(), false);
    }

    private void onPrisonerRelease(PrisonerReleaseEvent event) {
        handle(event.prisoner(), true);
    }

    private void handle(Prisoner prisoner, boolean released) {
        String jail = prisoner.jail().name();
        String player = prisoner.name();
        String operator = prisoner.jailedBy();

        PunishmentInfo punishment = new PunishmentInfo(released ? Type.UNJAIL : Type.JAIL);

        long durationMillis = prisoner.jailedUntil().toEpochMilli() - System.currentTimeMillis();

        punishment.setJail(jail);
        punishment.setPlayer(player);
        punishment.setOperator(operator);
        punishment.setDuration(TimeUtils.parseMillis(durationMillis));

        if (released && durationMillis <= 0) {
            punishment.setOperator(getPlugin().getAnnouncer().getConfig().getExpiredOperatorName());
        }

        handlePunishment(punishment);
    }
}
