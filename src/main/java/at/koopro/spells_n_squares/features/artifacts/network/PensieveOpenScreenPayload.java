package at.koopro.spells_n_squares.features.artifacts.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

/**
 * Network payload for opening Pensieve memory viewing screen on client.
 * Sent from server to client when Pensieve item is used.
 */
public record PensieveOpenScreenPayload(List<MemoryData> memories) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PensieveOpenScreenPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("pensieve_open_screen"));
    
    public record MemoryData(
        String description,
        long timestamp,
        String location
    ) {
        public static final StreamCodec<ByteBuf, MemoryData> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MemoryData::description,
            ByteBufCodecs.LONG,
            MemoryData::timestamp,
            ByteBufCodecs.STRING_UTF8,
            MemoryData::location,
            MemoryData::new
        );
    }
    
    public static final StreamCodec<ByteBuf, PensieveOpenScreenPayload> STREAM_CODEC = StreamCodec.composite(
        MemoryData.CODEC.apply(ByteBufCodecs.list()),
        PensieveOpenScreenPayload::memories,
        PensieveOpenScreenPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}


