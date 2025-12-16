package at.koopro.spells_n_squares.features.spell.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;

/**
 * Client -> server payload to toggle lumos on a held wand.
 */
public record LumosTogglePayload(InteractionHand hand) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LumosTogglePayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("lumos_toggle"));

    public static final StreamCodec<ByteBuf, LumosTogglePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        payload -> payload.hand().ordinal(),
        ordinal -> new LumosTogglePayload(InteractionHand.values()[ordinal])
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

