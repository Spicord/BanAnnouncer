package eu.mcdb.ban_announcer.bukkit.listener;

import com.github.fefo.betterjails.api.BetterJails;
import com.github.fefo.betterjails.api.event.prisoner.PlayerImprisonEvent;
import com.github.fefo.betterjails.api.event.prisoner.PrisonerReleaseEvent;
import com.github.fefo.betterjails.api.model.prisoner.Prisoner;

import eu.mcdb.ban_announcer.PunishmentAction;
import eu.mcdb.ban_announcer.PunishmentAction.Type;
import eu.mcdb.ban_announcer.bukkit.BanAnnouncerBukkit;
import eu.mcdb.ban_announcer.bukkit.BukkitPunishmentListener;

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

        PunishmentAction punishment = new PunishmentAction(released ? Type.UNJAIL : Type.JAIL);

        punishment.setJail(jail);
        punishment.setPlayer(player);
        punishment.setOperator(operator);

        handlePunishment(punishment);
    }
}
