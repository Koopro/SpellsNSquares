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

/**
 * Network payload for waypoint management operations.
 * Used for syncing waypoint list and management operations between client and server.
 */
public record WaypointManagementPayload(
    ManagementAction action,
    String waypointName,
    String newName, // For rename operations
    ResourceKey<Level> dimension, // For create operations
    BlockPos position // For create operations
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WaypointManagementPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("waypoint_management"));
    
    public enum ManagementAction {
        REQUEST_LIST,    // Client requests waypoint list
        CREATE,          // Client creates a waypoint
        DELETE,          // Client deletes a waypoint
        RENAME,          // Client renames a waypoint
        TELEPORT         // Client teleports to a waypoint
    }
    
    public static final StreamCodec<ByteBuf, ManagementAction> ACTION_CODEC = 
        ByteBufCodecs.stringUtf8(32).map(
            s -> ManagementAction.valueOf(s),
            ManagementAction::name
        );
    
    // Helper codec for nullable ResourceKey
    private static final StreamCodec<ByteBuf, ResourceKey<Level>> DIMENSION_CODEC = 
        ByteBufCodecs.fromCodec(ResourceKey.codec(Registries.DIMENSION));
    
    public static final StreamCodec<ByteBuf, WaypointManagementPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public WaypointManagementPayload decode(ByteBuf buffer) {
            ManagementAction action = ACTION_CODEC.decode(buffer);
            String waypointName = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String newName = ByteBufCodecs.STRING_UTF8.decode(buffer);
            boolean hasDimension = buffer.readBoolean();
            ResourceKey<Level> dimension = hasDimension ? DIMENSION_CODEC.decode(buffer) : null;
            boolean hasPosition = buffer.readBoolean();
            BlockPos position = hasPosition ? BlockPos.STREAM_CODEC.decode(buffer) : null;
            return new WaypointManagementPayload(action, waypointName, newName, dimension, position);
        }
        
        @Override
        public void encode(ByteBuf buffer, WaypointManagementPayload payload) {
            ACTION_CODEC.encode(buffer, payload.action());
            ByteBufCodecs.STRING_UTF8.encode(buffer, payload.waypointName());
            ByteBufCodecs.STRING_UTF8.encode(buffer, payload.newName() != null ? payload.newName() : "");
            buffer.writeBoolean(payload.dimension() != null);
            if (payload.dimension() != null) {
                DIMENSION_CODEC.encode(buffer, payload.dimension());
            }
            buffer.writeBoolean(payload.position() != null);
            if (payload.position() != null) {
                BlockPos.STREAM_CODEC.encode(buffer, payload.position());
            }
        }
    };
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    // Factory methods for common operations
    public static WaypointManagementPayload requestList() {
        return new WaypointManagementPayload(ManagementAction.REQUEST_LIST, "", null, null, null);
    }
    
    public static WaypointManagementPayload create(String name, ResourceKey<Level> dimension, BlockPos position) {
        return new WaypointManagementPayload(ManagementAction.CREATE, name, null, dimension, position);
    }
    
    public static WaypointManagementPayload delete(String waypointName) {
        return new WaypointManagementPayload(ManagementAction.DELETE, waypointName, null, null, null);
    }
    
    public static WaypointManagementPayload rename(String waypointName, String newName) {
        return new WaypointManagementPayload(ManagementAction.RENAME, waypointName, newName, null, null);
    }
    
    public static WaypointManagementPayload teleport(String waypointName) {
        return new WaypointManagementPayload(ManagementAction.TELEPORT, waypointName, null, null, null);
    }
}

