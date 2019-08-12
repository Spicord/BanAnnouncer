package eu.mcdb.ban_announcer.listener;

import eu.mcdb.ban_announcer.BAPunishment;
import eu.mcdb.ban_announcer.BAPunishment.Type;
import eu.mcdb.ban_announcer.BanAnnouncer;
import me.leoko.advancedban.utils.Punishment;

public class AdvancedBan {

    public static void onPunishment(Punishment abp) {
        BAPunishment bap = new BAPunishment();

        bap.setDuration(abp.getDuration(true));
        bap.setOperator(abp.getOperator());
        bap.setReason(abp.getReason());
        bap.setPlayer(abp.getName());
        bap.setPermanent(bap.getDuration().equals("permanent"));

        switch (abp.getType()) {
        case KICK:
            bap.setType(Type.KICK);
            break;
        case BAN:
            bap.setType(Type.BAN);
            break;
        case TEMP_BAN:
            bap.setType(Type.TEMPBAN);
            break;
        case IP_BAN:
            bap.setType(Type.BANIP);
            break;
        case TEMP_IP_BAN:
            bap.setType(Type.TEMPBANIP);
            break;
        case MUTE:
            bap.setType(Type.MUTE);
            break;
        case TEMP_MUTE:
            bap.setType(Type.TEMPMUTE);
            break;
        case WARNING:
            bap.setType(Type.WARN);
            break;
        case TEMP_WARNING:
            bap.setType(Type.TEMPWARN);
            break;
        }

        BanAnnouncer.getInstance().handlePunishment(bap);
    }
}
