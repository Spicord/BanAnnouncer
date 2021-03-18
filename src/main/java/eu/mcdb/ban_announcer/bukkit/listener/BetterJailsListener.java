package eu.mcdb.ban_announcer.bukkit.listener;

import com.github.fefo.betterjails.api.BetterJails;
import com.github.fefo.betterjails.api.event.prisoner.PlayerImprisonEvent;
import com.github.fefo.betterjails.api.event.prisoner.PrisonerReleaseEvent;
import com.github.fefo.betterjails.api.model.prisoner.Prisoner;
import eu.mcdb.ban_announcer.BanAnnouncer;
import eu.mcdb.ban_announcer.PunishmentAction;
import eu.mcdb.ban_announcer.PunishmentAction.Type;
import eu.mcdb.ban_announcer.bukkit.BanAnnouncerBukkit;

public class BetterJailsListener {

    private final BanAnnouncerBukkit plugin;
    private final BetterJails betterJails;

    public BetterJailsListener(BanAnnouncerBukkit plugin) {
        this.plugin = plugin;
        this.betterJails = plugin.getServer().getServicesManager().load(BetterJails.class);
    }

    public void subscribe() {
        betterJails.getEventBus().subscribe(plugin, PlayerImprisonEvent.class, this::onPlayerImprison);
        betterJails.getEventBus().subscribe(plugin, PrisonerReleaseEvent.class, this::onPrisonerRelease);
    }

    public void unsubscribe() {
        betterJails.getEventBus().unsubscribe(plugin);
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

        PunishmentAction pun = new PunishmentAction(released ? Type.UNJAIL : Type.JAIL);

        pun.setJail(jail);
        pun.setPlayer(player);
        pun.setOperator(operator);

        BanAnnouncer.getInstance().handlePunishmentAction(pun);
    }
}
