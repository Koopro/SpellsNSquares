package at.koopro.spells_n_squares.features.playerclass.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * Network payload for syncing player classes from server to client.
 * Supports multiple classes per player.
 */
public record PlayerClassSyncPayload(
    Set<PlayerClass> playerClasses
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerClassSyncPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("player_class_sync"));
    
    private static final StreamCodec<ByteBuf, PlayerClass> PLAYER_CLASS_CODEC = 
        ByteBufCodecs.STRING_UTF8.map(
            PlayerClass::fromName,
            PlayerClass::name
        );
    
    public static final StreamCodec<ByteBuf, PlayerClassSyncPayload> STREAM_CODEC = StreamCodec.composite(
        PLAYER_CLASS_CODEC.apply(ByteBufCodecs.collection(HashSet::new)),
        PlayerClassSyncPayload::playerClasses,
        PlayerClassSyncPayload::new
    );
    
    /**
     * Gets the primary class for backward compatibility.
     * @return The primary class, or NONE if no classes
     */
    public PlayerClass getPrimaryClass() {
        if (playerClasses == null || playerClasses.isEmpty()) {
            return PlayerClass.NONE;
        }
        
        // Priority order: BASE > ROLE > TRANSFORMATION > ALIGNMENT > ORGANIZATION > BLOOD_STATUS
        for (PlayerClass clazz : playerClasses) {
            if (clazz.getCategory() == at.koopro.spells_n_squares.features.playerclass.ClassCategory.BASE) {
                return clazz;
            }
        }
        for (PlayerClass clazz : playerClasses) {
            if (clazz.getCategory() == at.koopro.spells_n_squares.features.playerclass.ClassCategory.ROLE) {
                return clazz;
            }
        }
        for (PlayerClass clazz : playerClasses) {
            if (clazz.getCategory() == at.koopro.spells_n_squares.features.playerclass.ClassCategory.TRANSFORMATION) {
                return clazz;
            }
        }
        for (PlayerClass clazz : playerClasses) {
            if (clazz.getCategory() == at.koopro.spells_n_squares.features.playerclass.ClassCategory.ALIGNMENT) {
                return clazz;
            }
        }
        for (PlayerClass clazz : playerClasses) {
            if (clazz.getCategory() == at.koopro.spells_n_squares.features.playerclass.ClassCategory.ORGANIZATION) {
                return clazz;
            }
        }
        for (PlayerClass clazz : playerClasses) {
            if (clazz.getCategory() == at.koopro.spells_n_squares.features.playerclass.ClassCategory.BLOOD_STATUS) {
                return clazz;
            }
        }
        
        // Fallback: return first class
        return playerClasses.iterator().next();
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
