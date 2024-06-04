package me.tini.announcer;

import me.tini.announcer.extension.ExtensionContainer;

public abstract class PunishmentListener {

    private final BanAnnouncer announcer;

    private ExtensionContainer myLoader;

    public PunishmentListener(BanAnnouncer announcer) {
        this.announcer = announcer;
    }

    public final void handlePunishment(PunishmentInfo punishment) {
        announcer.handlePunishment(punishment, this);
    }

    public BanAnnouncer getAnnouncer() {
        return announcer;
    }

    public ExtensionContainer getExtensionLoader() {
        if (myLoader != null) {
            return myLoader;
        }
        for (ExtensionContainer loader : announcer.getExtensions()) {
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
