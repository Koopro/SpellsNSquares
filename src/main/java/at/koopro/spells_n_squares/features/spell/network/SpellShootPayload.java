package at.koopro.spells_n_squares.features.spell.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Network payload for shooting entities/blocks away while holding a spell.
 * Sent from client to server when player right-clicks while holding a hold spell.
 */
public record SpellShootPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SpellShootPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("spell_shoot"));
    
    public static final StreamCodec<ByteBuf, SpellShootPayload> STREAM_CODEC = StreamCodec.unit(
        new SpellShootPayload()
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}


