package at.koopro.spells_n_squares.features.transportation;

import at.koopro.spells_n_squares.core.data.DataComponentHelper;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * Manages waypoints for Apparition spell teleportation.
 * Waypoints are stored per player and can be saved, listed, and teleported to.
 */
public final class WaypointManager {
    
    private static final String DATA_KEY = "spells_n_squares:waypoints";
    private static final int MAX_WAYPOINTS = 10;
    
    private WaypointManager() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents a waypoint location.
     */
    public record WaypointData(
        UUID waypointId,
        String name,
        ResourceKey<Level> dimension,
        BlockPos position,
        long creationTime
    ) {
        public static final Codec<WaypointData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("waypointId").forGetter(WaypointData::waypointId),
                Codec.STRING.fieldOf("name").forGetter(WaypointData::name),
                ResourceKey.codec(net.minecraft.core.registries.Registries.DIMENSION)
                    .fieldOf("dimension").forGetter(WaypointData::dimension),
                BlockPos.CODEC.fieldOf("position").forGetter(WaypointData::position),
                Codec.LONG.fieldOf("creationTime").forGetter(WaypointData::creationTime)
            ).apply(instance, WaypointData::new)
        );
    }
    
    /**
     * Container for all waypoints for a player.
     */
    public record WaypointContainer(
        List<WaypointData> waypoints
    ) {
        public static final Codec<WaypointContainer> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(WaypointData.CODEC).fieldOf("waypoints").forGetter(WaypointContainer::waypoints)
            ).apply(instance, WaypointContainer::new)
        );
        
        public static WaypointContainer empty() {
            return new WaypointContainer(new ArrayList<>());
        }
    }
    
    /**
     * Saves the current player location as a waypoint.
     * 
     * @param player The player
     * @param name The waypoint name
     * @return true if waypoint was saved, false if max waypoints reached
     */
    public static boolean saveWaypoint(ServerPlayer player, String name) {
        if (player == null || name == null || name.trim().isEmpty()) {
            return false;
        }
        
        WaypointContainer container = DataComponentHelper.get(
            player, DATA_KEY, WaypointContainer.CODEC, WaypointContainer::empty);
        
        if (container.waypoints().size() >= MAX_WAYPOINTS) {
            return false;
        }
        
        WaypointData waypoint = new WaypointData(
            UUID.randomUUID(),
            name.trim(),
            player.level().dimension(),
            player.blockPosition(),
            System.currentTimeMillis()
        );
        
        List<WaypointData> updatedWaypoints = new ArrayList<>(container.waypoints());
        updatedWaypoints.add(waypoint);
        
        DataComponentHelper.set(player, DATA_KEY, WaypointContainer.CODEC, 
            new WaypointContainer(updatedWaypoints));
        
        DevLogger.logStateChange(WaypointManager.class, "saveWaypoint",
            "Saved waypoint: " + name + " at " + waypoint.position());
        
        return true;
    }
    
    /**
     * Gets all waypoints for a player.
     * 
     * @param player The player
     * @return List of waypoints
     */
    public static List<WaypointData> getWaypoints(Player player) {
        if (player == null) {
            return Collections.emptyList();
        }
        
        WaypointContainer container = DataComponentHelper.get(
            player, DATA_KEY, WaypointContainer.CODEC, WaypointContainer::empty);
        
        return new ArrayList<>(container.waypoints());
    }
    
    /**
     * Gets a waypoint by ID.
     * 
     * @param player The player
     * @param waypointId The waypoint ID
     * @return Optional waypoint, or empty if not found
     */
    public static Optional<WaypointData> getWaypoint(Player player, UUID waypointId) {
        return getWaypoints(player).stream()
            .filter(w -> w.waypointId().equals(waypointId))
            .findFirst();
    }
    
    /**
     * Deletes a waypoint.
     * 
     * @param player The player
     * @param waypointId The waypoint ID
     * @return true if waypoint was deleted
     */
    public static boolean deleteWaypoint(ServerPlayer player, UUID waypointId) {
        if (player == null) {
            return false;
        }
        
        WaypointContainer container = DataComponentHelper.get(
            player, DATA_KEY, WaypointContainer.CODEC, WaypointContainer::empty);
        
        List<WaypointData> updatedWaypoints = new ArrayList<>(container.waypoints());
        boolean removed = updatedWaypoints.removeIf(w -> w.waypointId().equals(waypointId));
        
        if (removed) {
            DataComponentHelper.set(player, DATA_KEY, WaypointContainer.CODEC,
                new WaypointContainer(updatedWaypoints));
            DevLogger.logStateChange(WaypointManager.class, "deleteWaypoint",
                "Deleted waypoint: " + waypointId);
        }
        
        return removed;
    }
    
    /**
     * Teleports player to a waypoint.
     * 
     * @param player The player
     * @param waypointId The waypoint ID
     * @param level The server level
     * @return true if teleportation was successful
     */
    public static boolean teleportToWaypoint(ServerPlayer player, UUID waypointId, ServerLevel level) {
        if (player == null || level == null) {
            return false;
        }
        
        Optional<WaypointData> waypointOpt = getWaypoint(player, waypointId);
        if (waypointOpt.isEmpty()) {
            return false;
        }
        
        WaypointData waypoint = waypointOpt.get();
        
        // Check if player is in the same dimension
        if (!level.dimension().equals(waypoint.dimension())) {
            return false;
        }
        
        // Find safe position near waypoint
        Vec3 targetPos = findSafePosition(level, waypoint.position());
        if (targetPos == null) {
            return false;
        }
        
        // Teleport player
        player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
        
        return true;
    }
    
    /**
     * Finds a safe position near the given block position.
     */
    private static Vec3 findSafePosition(ServerLevel level, BlockPos pos) {
        BlockPos startPos = pos.above();
        
        for (int i = 0; i < 5; i++) {
            BlockPos feetPos = startPos.offset(0, i, 0);
            BlockPos headPos = feetPos.above();
            
            if (isSafePosition(level, feetPos) && isSafePosition(level, headPos)) {
                BlockPos groundPos = feetPos.below();
                net.minecraft.world.level.block.state.BlockState groundState = level.getBlockState(groundPos);
                if (!groundState.isAir() && groundState.getFluidState().isEmpty()) {
                    return new Vec3(feetPos.getX() + 0.5, feetPos.getY(), feetPos.getZ() + 0.5);
                }
            }
        }
        
        // Fallback to position above
        return new Vec3(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
    }
    
    private static boolean isSafePosition(ServerLevel level, BlockPos pos) {
        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
        return (state.isAir() || state.getCollisionShape(level, pos).isEmpty()) && 
               !state.getFluidState().is(net.minecraft.tags.FluidTags.LAVA);
    }
}

