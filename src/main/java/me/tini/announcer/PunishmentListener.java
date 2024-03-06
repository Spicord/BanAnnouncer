package me.tini.announcer;

public abstract class PunishmentListener {

    private final BanAnnouncer announcer;

    public PunishmentListener(BanAnnouncer announcer) {
        this.announcer = announcer;
    }

    public final void handlePunishment(PunishmentInfo punishment) {
        announcer.handlePunishment(punishment);
    }

    public BanAnnouncer getAnnouncer() {
        return announcer;
    }

    public abstract void register();

    public abstract void unregister();

}
