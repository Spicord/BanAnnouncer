package eu.mcdb.ban_announcer.listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import eu.mcdb.ban_announcer.BanAnnouncerPlugin;
import eu.mcdb.ban_announcer.PunishmentAction;
import eu.mcdb.ban_announcer.PunishmentListener;
import eu.mcdb.ban_announcer.utils.TimeUtils;
import space.arim.libertybans.api.AddressVictim;
import space.arim.libertybans.api.LibertyBans;
import space.arim.libertybans.api.Operator;
import space.arim.libertybans.api.Operator.OperatorType;
import space.arim.libertybans.api.PlayerOperator;
import space.arim.libertybans.api.PlayerVictim;
import space.arim.libertybans.api.Victim;
import space.arim.libertybans.api.Victim.VictimType;
import space.arim.libertybans.api.event.PostPardonEvent;
import space.arim.libertybans.api.event.PostPunishEvent;
import space.arim.libertybans.api.punish.Punishment;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.RegisteredListener;

public class LibertyBansListener extends PunishmentListener {

    private final LibertyBans libertyBans = findLibertyBansInstance();
    private final EventBus eventBus;

    private RegisteredListener postPunishListenerMarker;
    private RegisteredListener postPardonListenerMarker;

    public LibertyBansListener(BanAnnouncerPlugin plugin) {
        super(plugin.getAnnouncer());
        eventBus = libertyBans.getOmnibus().getEventBus();
    }

    @Override
    public void register() {
        if (isRegistered()) {
            throw new IllegalStateException("call unregister() first");
        }
        postPunishListenerMarker = eventBus.registerListener(PostPunishEvent.class, (byte) 0, this::onPostPunishEvent);
        postPardonListenerMarker = eventBus.registerListener(PostPardonEvent.class, (byte) 0, this::onPostPardonEvent);
    }

    @Override
    public void unregister() {
        if (isRegistered()) {
            eventBus.unregisterListener(postPunishListenerMarker);
            eventBus.unregisterListener(postPardonListenerMarker);
        }
        postPunishListenerMarker = null;
        postPardonListenerMarker = null;
    }

    private boolean isRegistered() {
        return postPunishListenerMarker != null || postPardonListenerMarker != null;
    }

    private void onPostPunishEvent(PostPunishEvent event) {
        handle(event.getPunishment().getOperator(), event.getPunishment(), false);
    }

    private void onPostPardonEvent(PostPardonEvent event) {
        handle(event.getOperator(), event.getPunishment(), true);
    }

    private void handle(Operator operator, Punishment pun, boolean isRevoked) {
        PunishmentAction punishment = new PunishmentAction();

        boolean isConsole = operator.getType() == OperatorType.CONSOLE;

        String operatorName = isConsole
                ? getAnnouncer().getConfig().getConsoleName()
                : getOperatorName(operator);

        punishment.setId(Integer.toString(pun.getID()));
        punishment.setOperator(operatorName);
        punishment.setPlayer(getVictimName(pun.getVictim()));
        punishment.setReason(pun.getReason());
        punishment.setPermanent(pun.isPermanent());

        if (punishment.isPermanent()) {
            punishment.setDuration("permanent");
        } else {
            punishment.setDuration(TimeUtils.parseMillis(pun.getEndDateSeconds() * 1000));
        }

        switch (pun.getType()) {
        case BAN:
            boolean isBanIP = pun.getVictim().getType() == VictimType.ADDRESS;

            if (isBanIP) {
                if (isRevoked) {
                    punishment.setType(PunishmentAction.Type.UNBANIP);
                } else {
                    punishment.setType(punishment.isPermanent() ? PunishmentAction.Type.BANIP : PunishmentAction.Type.TEMPBANIP);
                }
            } else {
                if (isRevoked) {
                    punishment.setType(PunishmentAction.Type.UNBAN);
                } else {
                    punishment.setType(punishment.isPermanent() ? PunishmentAction.Type.BAN : PunishmentAction.Type.TEMPBAN);
                }
            }

            break;
        case KICK:
            punishment.setType(PunishmentAction.Type.KICK);
            break;
        case MUTE:
            if (isRevoked) {
                punishment.setType(PunishmentAction.Type.UNMUTE);
            } else {
                punishment.setType(punishment.isPermanent() ? PunishmentAction.Type.MUTE : PunishmentAction.Type.TEMPMUTE);
            }
            break;
        case WARN:
            if (isRevoked) {
                punishment.setType(PunishmentAction.Type.UNWARN);
            } else {
                punishment.setType(punishment.isPermanent() ? PunishmentAction.Type.WARN : PunishmentAction.Type.TEMPWARN);
            }
            break;
        default:
            break;
        }

        handlePunishment(punishment);
    }

    private String getVictimName(Victim victim) {
        if (victim.getType() == VictimType.PLAYER) {
            return getPlayerName(((PlayerVictim) victim).getUUID());
        }
        if (victim.getType() == VictimType.ADDRESS) {
            return ((AddressVictim) victim).getAddress().toInetAddress().getHostAddress();
        }
        return "<Unknown>";
    }

    private String getOperatorName(Operator operator) {
        return (operator.getType() == OperatorType.CONSOLE)
               ? getAnnouncer().getConfig().getConsoleName()
               : getPlayerName(((PlayerOperator) operator).getUUID());
    }

    private String getPlayerName(UUID uuid) {
        final String query = "select `name` from `libertybans_names` where `uuid` = ?";

        try (
            Connection conn = libertyBans.getDatabase().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {

            stmt.setBytes(1, getUuidBytes(uuid));

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("name") : "<Unknown>";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "<DatabaseError>";
    }

    private byte[] getUuidBytes(UUID uuid) {
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();

        byte[] uuidAsBytes = new byte[16];

        uuidAsBytes[0] = (byte)(mostSignificantBits >>> 56);
        uuidAsBytes[1] = (byte)(mostSignificantBits >>> 48);
        uuidAsBytes[2] = (byte)(mostSignificantBits >>> 40);
        uuidAsBytes[3] = (byte)(mostSignificantBits >>> 32);
        uuidAsBytes[4] = (byte)(mostSignificantBits >>> 24);
        uuidAsBytes[5] = (byte)(mostSignificantBits >>> 16);
        uuidAsBytes[6] = (byte)(mostSignificantBits >>>  8);
        uuidAsBytes[7] = (byte)(mostSignificantBits >>>  0);
        uuidAsBytes[8] = (byte)(leastSignificantBits >>> 56);
        uuidAsBytes[9] = (byte)(leastSignificantBits >>> 48);
        uuidAsBytes[10] = (byte)(leastSignificantBits >>> 40);
        uuidAsBytes[11] = (byte)(leastSignificantBits >>> 32);
        uuidAsBytes[12] = (byte)(leastSignificantBits >>> 24);
        uuidAsBytes[13] = (byte)(leastSignificantBits >>> 16);
        uuidAsBytes[14] = (byte)(leastSignificantBits >>>  8);
        uuidAsBytes[15] = (byte)(leastSignificantBits >>>  0);

        return uuidAsBytes;
    }

    private static LibertyBans findLibertyBansInstance() {
         Omnibus omnibus = OmnibusProvider.getOmnibus();
         Optional<LibertyBans> instance = omnibus.getRegistry().getProvider(LibertyBans.class);
         if (!instance.isPresent()) {
             throw new IllegalStateException("LibertyBans not found");
         }
         return instance.get();
    }
}
