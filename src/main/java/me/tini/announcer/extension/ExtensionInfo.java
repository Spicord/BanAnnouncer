package me.tini.announcer.extension;

import com.google.gson.annotations.SerializedName;

public class ExtensionInfo {

    private String name;

    private String key;

    @SerializedName("class")
    private String clazz;

    private String requiredClass;

    private boolean isPunishmentManager;

    private boolean isJailManager;

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getClassName() {
        return clazz;
    }

    public String getRequiredClass() {
        return requiredClass;
    }

    public boolean isPunishmentManager() {
        return isPunishmentManager;
    }

    public boolean isJailManager() {
        return isJailManager;
    }
}
