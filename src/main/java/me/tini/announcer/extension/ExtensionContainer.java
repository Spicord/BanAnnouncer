package me.tini.announcer.extension;

import java.util.function.Supplier;

import me.tini.announcer.BanAnnouncerPlugin;

public class ExtensionContainer {

    ExtensionInfo info;
    Supplier<AbstractExtension> instanceSupplier;
    AbstractExtension instance;

    ExtensionContainer() {}

    public ExtensionContainer(ExtensionInfo info, Supplier<AbstractExtension> instanceSupplier) {
        this.info = info;
        this.instanceSupplier = instanceSupplier;
    }

    public ExtensionInfo getInfo() {
        return info;
    }

    public Supplier<AbstractExtension> getInstanceSupplier(BanAnnouncerPlugin plugin) {
        if (instance != null) {
            return () -> instance;
        }
        return () -> (instance = instanceSupplier.get());
    }

    public boolean isInstanceCreated() {
        return instance != null;
    }

    public AbstractExtension getInstance() {
        return instance;
    }

    public void close() {
    }
}
