package at.koopro.spells_n_squares.features.artifacts.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;
import java.util.UUID;

/**
 * Network payload for opening Goblet of Fire tournament screen on client.
 * Sent from server to client when Goblet of Fire item is used.
 */
public record GobletOfFirePayload(
    List<ParticipantData> participants,
    List<UUID> champions,
    boolean tournamentActive
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GobletOfFirePayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("goblet_of_fire_open"));
    
    public record ParticipantData(
        UUID playerId,
        String playerName,
        long entryTick
    ) {
        public static final StreamCodec<ByteBuf, ParticipantData> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString),
            ParticipantData::playerId,
            ByteBufCodecs.STRING_UTF8,
            ParticipantData::playerName,
            ByteBufCodecs.LONG,
            ParticipantData::entryTick,
            ParticipantData::new
        );
    }
    
    public static final StreamCodec<ByteBuf, GobletOfFirePayload> STREAM_CODEC = StreamCodec.composite(
        ParticipantData.CODEC.apply(ByteBufCodecs.list()),
        GobletOfFirePayload::participants,
        ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString).apply(ByteBufCodecs.list()),
        GobletOfFirePayload::champions,
        ByteBufCodecs.BOOL,
        GobletOfFirePayload::tournamentActive,
        GobletOfFirePayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}


