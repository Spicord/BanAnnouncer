package me.tini.announcer;

import me.tini.announcer.extension.ExtensionLoader;

public abstract class PunishmentListener {

    private final BanAnnouncer announcer;

    private ExtensionLoader myLoader;

    public PunishmentListener(BanAnnouncer announcer) {
        this.announcer = announcer;
    }

    public final void handlePunishment(PunishmentInfo punishment) {
        announcer.handlePunishment(punishment, this);
    }

    public BanAnnouncer getAnnouncer() {
        return announcer;
    }

    public ExtensionLoader getExtensionLoader() {
        if (myLoader != null) {
            return myLoader;
        }
        for (ExtensionLoader loader : announcer.getExtensions()) {
            if (loader.isInstanceCreated()) {
                if (loader.getInstance().getPunishmentListener() == this) {
                    return myLoader = loader;
                }
            }
        }
        throw new IllegalStateException("Unable to get extension");
    }

    public String getName() {
        return getExtensionLoader().getInfo().getName();
    }

    public abstract void register();

    public abstract void unregister();

}
