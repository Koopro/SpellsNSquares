package at.koopro.spells_n_squares.features.spell.network;

import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Network payload for casting a spell.
 * Sent from client to server when player casts a spell from a slot.
 */
public record SpellCastPayload(int slot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SpellCastPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("spell_cast"));
    
    public static final StreamCodec<ByteBuf, SpellCastPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        SpellCastPayload::slot,
        SpellCastPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}














