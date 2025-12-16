package at.koopro.spells_n_squares.features.convenience.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Network payload for waypoint selection for Apparition spell.
 * Sent from client to server when player selects a waypoint to teleport to.
 */
public record WaypointSelectionPayload(String waypointName) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WaypointSelectionPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("waypoint_selection"));
    
    public static final StreamCodec<ByteBuf, WaypointSelectionPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        WaypointSelectionPayload::waypointName,
        WaypointSelectionPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
