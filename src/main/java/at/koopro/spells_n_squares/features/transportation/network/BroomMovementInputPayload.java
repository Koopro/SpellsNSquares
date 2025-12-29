package at.koopro.spells_n_squares.features.transportation.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Payload for sending broom movement input from client to server.
 */
public record BroomMovementInputPayload(int entityId, float forward, float strafe, boolean jump) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BroomMovementInputPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("broom_movement_input"));
    
    public static final StreamCodec<ByteBuf, BroomMovementInputPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        BroomMovementInputPayload::entityId,
        ByteBufCodecs.FLOAT,
        BroomMovementInputPayload::forward,
        ByteBufCodecs.FLOAT,
        BroomMovementInputPayload::strafe,
        ByteBufCodecs.BOOL,
        BroomMovementInputPayload::jump,
        BroomMovementInputPayload::new
    );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

