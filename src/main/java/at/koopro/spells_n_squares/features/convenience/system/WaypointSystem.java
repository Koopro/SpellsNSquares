package at.koopro.spells_n_squares.features.convenience.system;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * System for managing waypoints and teleportation.
 */
public final class WaypointSystem {
    private WaypointSystem() {
    }
    
    // Map of player UUID to their waypoints
    // Using HashMap - order doesn't matter, O(1) lookup needed
    private static final Map<UUID, List<Waypoint>> playerWaypoints = new HashMap<>();
    
    // Splinch chance for apparition teleportation
    private static final double SPLINCH_CHANCE = 0.05; // 5% base chance
    
    /**
     * Represents a waypoint.
     */
    public record Waypoint(
        String name,
        ResourceKey<Level> dimension,
        BlockPos position,
        long createdTick
    ) {
    }
    
    /**
     * Creates a waypoint for a player.
     */
    public static void createWaypoint(ServerPlayer player, String name) {
        UUID playerId = player.getUUID();
        BlockPos pos = player.blockPosition();
        ResourceKey<Level> dimension = player.level().dimension();
        long createdTick = ((ServerLevel) player.level()).getGameTime();
        
        Waypoint waypoint = new Waypoint(name, dimension, pos, createdTick);
        playerWaypoints.computeIfAbsent(playerId, k -> new ArrayList<>()).add(waypoint);
        
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.waypoint.created", name));
    }
    
    /**
     * Teleports a player to a waypoint.
     */
    public static boolean teleportToWaypoint(ServerPlayer player, String waypointName) {
        UUID playerId = player.getUUID();
        List<Waypoint> waypoints = playerWaypoints.get(playerId);
        
        if (waypoints == null) {
            return false;
        }
        
        Optional<Waypoint> waypoint = waypoints.stream()
            .filter(w -> w.name().equals(waypointName))
            .findFirst();
        
        if (waypoint.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.waypoint.not_found", waypointName));
            return false;
        }
        
        Waypoint target = waypoint.get();
        ServerLevel targetLevel = ((ServerLevel) player.level()).getServer().getLevel(target.dimension());
        
        if (targetLevel == null) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.waypoint.cannot_teleport"));
            return false;
        }
        
        // Visual effect at origin
        Vec3 origin = player.position();
        ((ServerLevel) player.level()).sendParticles(ParticleTypes.PORTAL,
            origin.x, origin.y, origin.z,
            30, 0.5, 0.5, 0.5, 0.1);
        
        // Teleport
        BlockPos targetPos = target.position();
        player.teleportTo(targetLevel, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5,
            java.util.Set.of(), player.getYRot(), player.getXRot(), false);
        
        // Visual effect at destination
        Vec3 dest = Vec3.atCenterOf(targetPos);
        targetLevel.sendParticles(ParticleTypes.PORTAL,
            dest.x, dest.y, dest.z,
            30, 0.5, 0.5, 0.5, 0.1);
        
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.waypoint.teleported", waypointName));
        return true;
    }
    
    /**
     * Gets all waypoints for a player.
     */
    public static List<Waypoint> getWaypoints(UUID playerId) {
        return playerWaypoints.getOrDefault(playerId, Collections.emptyList());
    }
    
    /**
     * Removes a waypoint.
     */
    public static boolean removeWaypoint(UUID playerId, String waypointName) {
        List<Waypoint> waypoints = playerWaypoints.get(playerId);
        if (waypoints == null) {
            return false;
        }
        
        return waypoints.removeIf(w -> w.name().equals(waypointName));
    }
    
    /**
     * Apparates a player to a waypoint with Apparition-style effects and Splinching risk.
     * Used by the Apparition spell when selecting from multiple waypoints.
     * 
     * @param player The player to teleport
     * @param waypointName The name of the waypoint to teleport to
     * @return true if teleportation was successful
     */
    public static boolean apparateToWaypoint(ServerPlayer player, String waypointName) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        
        List<Waypoint> waypoints = playerWaypoints.get(player.getUUID());
        if (waypoints == null) {
            return false;
        }
        
        Optional<Waypoint> waypointOpt = waypoints.stream()
            .filter(w -> w.name().equals(waypointName))
            .findFirst();
        
        if (waypointOpt.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.waypoint.not_found", waypointName));
            return false;
        }
        
        Waypoint waypoint = waypointOpt.get();
        ServerLevel targetLevel = serverLevel.getServer().getLevel(waypoint.dimension());
        
        if (targetLevel == null) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.waypoint.cannot_apparate"));
            return false;
        }
        
        Vec3 originPos = player.position();
        Vec3 destPos = new Vec3(
            waypoint.position().getX() + 0.5,
            waypoint.position().getY(),
            waypoint.position().getZ() + 0.5
        );
        
        // Apparition effects at origin
        spawnApparitionEffects(serverLevel, originPos, player.blockPosition());
        
        // Handle potential Splinching
        if (serverLevel.getRandom().nextDouble() < SPLINCH_CHANCE) {
            handleSplinching(player, serverLevel);
        }
        
        // Teleport player
        player.teleportTo(targetLevel, destPos.x, destPos.y, destPos.z,
            Set.of(), player.getYRot(), player.getXRot(), false);
        
        // Apparition effects at destination
        spawnApparitionEffects(targetLevel, destPos, player.blockPosition());
        
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.waypoint.apparated", waypoint.name()));
        return true;
    }
    
    /**
     * Spawns visual and sound effects for apparition.
     */
    private static void spawnApparitionEffects(ServerLevel level, Vec3 pos, BlockPos soundPos) {
        level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y, pos.z, 30, 0.5, 0.5, 0.5, 0.1);
        level.playSound(null, soundPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
    
    /**
     * Handles the Splinching effect when apparition goes wrong.
     */
    private static void handleSplinching(ServerPlayer player, ServerLevel level) {
        player.hurtServer(level, level.damageSources().magic(), 2.0f);
        player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 0));
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.waypoint.splinched"));
    }
}

