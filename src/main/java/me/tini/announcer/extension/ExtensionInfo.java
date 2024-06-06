package me.tini.announcer.extension;

import com.google.gson.annotations.SerializedName;

public class ExtensionInfo {

    private String name;

    @SerializedName(value = "id", alternate = { "key" })
    private String id;

    @SerializedName("class")
    private String mainClass;

    private String requiredClass;

    //###################################
    private boolean isPunishmentManager, isJailManager;
    //###################################

    public ExtensionInfo(String name, String id, String mainClass, String requiredClass) {
        this.name = name;
        this.id = id;
        this.mainClass = mainClass;
        this.requiredClass = requiredClass;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getRequiredClass() {
        return requiredClass;
    }

    @Deprecated
    public boolean hasPunishmentManager() {
        return isPunishmentManager || isJailManager;
    }
}
