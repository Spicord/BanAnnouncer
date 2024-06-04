package me.tini.announcer.extension;

import com.google.gson.annotations.SerializedName;

public class ExtensionInfo {

    private String name;

    @SerializedName(value = "id", alternate = { "key" })
    private String id;

    @SerializedName("class")
    private String clazz;

    private String requiredClass;

    //###################################
    private boolean isPunishmentManager, isJailManager;
    //###################################

    public ExtensionInfo(String name, String id, String mainClass, String requiredClass) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getMainClass() {
        return clazz;
    }

    public String getRequiredClass() {
        return requiredClass;
    }

    public boolean hasPunishmentManager() {
        return isPunishmentManager || isJailManager;
    }
}
