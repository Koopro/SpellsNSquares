package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Network payload for spell casting requests from client to server.
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
