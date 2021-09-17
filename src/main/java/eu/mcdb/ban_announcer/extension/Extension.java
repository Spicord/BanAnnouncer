package eu.mcdb.ban_announcer.extension;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

import com.google.gson.annotations.SerializedName;

import eu.mcdb.ban_announcer.BanAnnouncerPlugin;
import eu.mcdb.ban_announcer.PunishmentListener;

public class Extension {

    private transient ExtensionClassLoader loader;

    @SerializedName("name")
    private String _name;

    @SerializedName("key")
    private String _key;

    @SerializedName("class")
    private String _class;

    @SerializedName("requiredClass")
    private String _requiredClass;

    @SerializedName("isJailManager")
    private boolean _isJailManager;

    public String getName() {
        return _name;
    }

    public String getKey() {
        return _key;
    }

    public String getRequiredClass() {
        return _requiredClass;
    }

    public boolean isPunishmentManager() {
        return !_isJailManager;
    }

    public Supplier<PunishmentListener> getInstanceSupplier(BanAnnouncerPlugin plugin) {
        return () -> {
            try {
                Class<?> listenerClass = loader.loadClass(_class);
                Constructor<?> constructor = listenerClass.getConstructor(BanAnnouncerPlugin.class);
                return (PunishmentListener) constructor.newInstance(plugin);
            } catch (Exception e) {
                throw new ExtensionException("Failed to create listener instance", e);
            }
        };
    }

    protected void setClassLoader(ExtensionClassLoader loader) {
        this.loader = loader;
    }

    public ClassLoader getClassLoader() {
        return loader;
    }

    public void unload() {
        loader.close();
    }
}
