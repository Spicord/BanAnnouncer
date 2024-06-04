package me.tini.announcer.extension;

import me.tini.announcer.PunishmentInfo;
import me.tini.announcer.PunishmentListener;

public abstract class AbstractExtension {

    public String processPlaceholder(PunishmentInfo info, String placeholder) {
        return null;
    }

    public PunishmentListener getPunishmentListener() {
        return null;
    }
}
