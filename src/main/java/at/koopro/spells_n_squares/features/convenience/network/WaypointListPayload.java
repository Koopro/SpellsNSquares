package at.koopro.spells_n_squares.features.convenience.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Network payload for sending waypoint list to client for selection.
 * Sent from server to client when Apparition is cast with multiple waypoints.
 */
public record WaypointListPayload(List<WaypointData> waypoints) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WaypointListPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("waypoint_list"));
    
    public record WaypointData(
        String name,
        ResourceKey<Level> dimension,
        BlockPos position,
        long createdTick
    ) {
        public static final StreamCodec<ByteBuf, WaypointData> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            WaypointData::name,
            ByteBufCodecs.fromCodec(ResourceKey.codec(Registries.DIMENSION)),
            WaypointData::dimension,
            BlockPos.STREAM_CODEC,
            WaypointData::position,
            ByteBufCodecs.LONG,
            WaypointData::createdTick,
            WaypointData::new
        );
    }
    
    public static final StreamCodec<ByteBuf, WaypointListPayload> STREAM_CODEC = StreamCodec.composite(
        WaypointData.CODEC.apply(ByteBufCodecs.list()),
        WaypointListPayload::waypoints,
        WaypointListPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
