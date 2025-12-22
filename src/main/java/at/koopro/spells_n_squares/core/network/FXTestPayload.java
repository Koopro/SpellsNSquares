package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Network payload for triggering client-side FX effects from server commands.
 */
public record FXTestPayload(String effectType, float param1, float param2, int param3, String shaderType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FXTestPayload> TYPE = 
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("fx_test"));
    
    public static final StreamCodec<ByteBuf, FXTestPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        FXTestPayload::effectType,
        ByteBufCodecs.FLOAT,
        FXTestPayload::param1,
        ByteBufCodecs.FLOAT,
        FXTestPayload::param2,
        ByteBufCodecs.VAR_INT,
        FXTestPayload::param3,
        ByteBufCodecs.STRING_UTF8,
        FXTestPayload::shaderType,
        FXTestPayload::new
    );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}









