package at.koopro.spells_n_squares.features.playerclass.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Network payload for resolving class conflicts.
 * Sent from client to server when player chooses to replace a conflicting class.
 */
public record ClassConflictResolutionPayload(
    String spellId,
    PlayerClass newClass,
    PlayerClass classToRemove,
    boolean replace
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClassConflictResolutionPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("class_conflict_resolution"));
    
    private static final StreamCodec<ByteBuf, PlayerClass> PLAYER_CLASS_CODEC = 
        ByteBufCodecs.STRING_UTF8.map(
            PlayerClass::fromName,
            PlayerClass::name
        );
    
    public static final StreamCodec<ByteBuf, ClassConflictResolutionPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        ClassConflictResolutionPayload::spellId,
        PLAYER_CLASS_CODEC,
        ClassConflictResolutionPayload::newClass,
        PLAYER_CLASS_CODEC,
        ClassConflictResolutionPayload::classToRemove,
        ByteBufCodecs.BOOL,
        ClassConflictResolutionPayload::replace,
        ClassConflictResolutionPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}















