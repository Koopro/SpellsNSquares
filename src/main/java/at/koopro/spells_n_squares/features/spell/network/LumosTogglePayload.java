package at.koopro.spells_n_squares.features.spell.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;

/**
 * Network payload for toggling Lumos.
 * Sent from client to server when player toggles Lumos.
 */
public record LumosTogglePayload(InteractionHand hand) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LumosTogglePayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("lumos_toggle"));
    
    private static final StreamCodec<ByteBuf, InteractionHand> HAND_CODEC =
        ByteBufCodecs.BYTE.map(
            b -> b == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
            h -> (byte) (h == InteractionHand.MAIN_HAND ? 0 : 1)
        );
    
    public static final StreamCodec<ByteBuf, LumosTogglePayload> STREAM_CODEC = StreamCodec.composite(
        HAND_CODEC,
        LumosTogglePayload::hand,
        LumosTogglePayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
