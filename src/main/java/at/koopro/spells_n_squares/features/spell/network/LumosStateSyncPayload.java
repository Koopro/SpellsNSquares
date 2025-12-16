package at.koopro.spells_n_squares.features.spell.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Network payload for syncing Lumos state from server to client.
 */
public record LumosStateSyncPayload(boolean active) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LumosStateSyncPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("lumos_state_sync"));
    
    public static final StreamCodec<ByteBuf, LumosStateSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        LumosStateSyncPayload::active,
        LumosStateSyncPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
