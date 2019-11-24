package eu.mcdb.ban_announcer;

import eu.mcdb.ban_announcer.BAPunishment;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class BAUnpunishment {

    private String player = "";
    private String operator = "";
    private String reason = "";
    private BAUnpunishment.Type type = BAUnpunishment.Type.UNKNOWN;

    public BAUnpunishment() {}

    public BAUnpunishment(BAUnpunishment.Type type) {
        this.type = type;
    }

    public enum Type {
        UNKICK, UNBAN, UNMUTE, UNBANIP, UNWARN, MASS, UNKNOWN;
    }
}
