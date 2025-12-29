package at.koopro.spells_n_squares.features.artifacts.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;
import java.util.UUID;

/**
 * Network payload for opening Marauder's Map screen on client.
 * Sent from server to client when Marauder's Map item is used.
 */
public record MaraudersMapPayload(List<PlayerLocationData> playerLocations) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MaraudersMapPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("marauders_map_open"));
    
    public record PlayerLocationData(
        UUID playerId,
        String playerName,
        double x,
        double y,
        double z,
        long lastUpdateTick
    ) {
        public static final StreamCodec<ByteBuf, PlayerLocationData> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString),
            PlayerLocationData::playerId,
            ByteBufCodecs.STRING_UTF8,
            PlayerLocationData::playerName,
            ByteBufCodecs.DOUBLE,
            PlayerLocationData::x,
            ByteBufCodecs.DOUBLE,
            PlayerLocationData::y,
            ByteBufCodecs.DOUBLE,
            PlayerLocationData::z,
            ByteBufCodecs.LONG,
            PlayerLocationData::lastUpdateTick,
            PlayerLocationData::new
        );
    }
    
    public static final StreamCodec<ByteBuf, MaraudersMapPayload> STREAM_CODEC = StreamCodec.composite(
        PlayerLocationData.CODEC.apply(ByteBufCodecs.list()),
        MaraudersMapPayload::playerLocations,
        MaraudersMapPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}











