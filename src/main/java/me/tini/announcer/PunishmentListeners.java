package me.tini.announcer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

import me.tini.announcer.extension.AbstractExtension;

public class PunishmentListeners {

    private final Logger logger;
    private final Map<String, PunishmentManagerInfo> punishListeners = new HashMap<>();

    private final List<ActiveListenerEntry> activeListeners = new ArrayList<>();

    public PunishmentListeners(Logger logger) {
        this.logger = logger;
    }

    public void addNew(String name, String key, Supplier<AbstractExtension> listenerSupplier, boolean isPun, String classToDetect) {
        punishListeners.put(key, new PunishmentManagerInfo(name, listenerSupplier, classToDetect));
    }

    public void autoDetect() {
        for (PunishmentManagerInfo info : punishListeners.values()) {
            if (isClassPresent(info.getClassToDetect())) {
                PunishmentListener listener = info.newInstance();
                activeListeners.add(new ActiveListenerEntry(info, listener));
                listener.register();

                logger.info("[AutoDetect] Using " + info.getName() + " as the punishment manager.");
                return;
            }
        }
        logger.severe("[AutoDetect] No compatible plugin found. BanAnnouncer will not work!.");
    }

    public void startPunishListener(String key) {
        if (punishListeners.containsKey(key)) {
            PunishmentManagerInfo info = punishListeners.get(key);

            if (isClassPresent(info.getClassToDetect())) {
                PunishmentListener listener = info.newInstance();
                activeListeners.add(new ActiveListenerEntry(info, listener));
                listener.register();

                logger.info("Using " + info.getName() + " as the punishment manager.");
            } else {
                logger.severe("You choose " + info.getName() + " but you don't have it installed, BanAnnouncer will not work!.");
            }
        } else {
            logger.severe("You choose " + key + " but I don't know what that is!");
        }
    }

    public void startJailListener(String key) {
        if (punishListeners.containsKey(key)) {
            PunishmentManagerInfo info = punishListeners.get(key);

            if (isClassPresent(info.getClassToDetect())) {
                PunishmentListener listener = info.newInstance();
                activeListeners.add(new ActiveListenerEntry(info, listener));
                listener.register();

                logger.info("Using " + info.getName() + " as the jail manager.");
            } else {
                logger.severe("You choose " + info.getName() + " but you don't have it installed, BanAnnouncer will not work!.");
            }
        } else {
            logger.severe("You choose " + key + " but I don't know what that is!");
        }
    }

    public void stopAllListeners() {
        Iterator<ActiveListenerEntry> i = activeListeners.iterator();
        while (i.hasNext()) {
            ActiveListenerEntry activeListener = i.next();
            activeListener.getInstance().unregister();
            logger.info("Unregistered listener for " + activeListener.getInfo().getName());
            i.remove();
        }
    }

    private class ActiveListenerEntry {
        private final PunishmentManagerInfo info;
        private final PunishmentListener instance;

        public ActiveListenerEntry(PunishmentManagerInfo info, PunishmentListener instance) {
            this.info = info;
            this.instance = instance;
        }

        public PunishmentManagerInfo getInfo() {
            return info;
        }

        public PunishmentListener getInstance() {
            return instance;
        }
    }

    private class PunishmentManagerInfo {

        private final String name;
        private final Supplier<AbstractExtension> listenerSupplier;
        private final String classToDetect;

        PunishmentManagerInfo(String name, Supplier<AbstractExtension> listenerSupplier, String classToDetect) {
            this.name = name;
            this.listenerSupplier = listenerSupplier;
            this.classToDetect = classToDetect;
        }

        public String getName() {
            return name;
        }

        public PunishmentListener newInstance() {
            return listenerSupplier.get().getPunishmentListener();
        }

        public String getClassToDetect() {
            return classToDetect;
        }
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, PunishmentListeners.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {}
        return false;
    }
}
