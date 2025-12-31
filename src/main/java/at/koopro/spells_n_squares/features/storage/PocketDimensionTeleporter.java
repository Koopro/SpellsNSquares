package at.koopro.spells_n_squares.features.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

/**
 * Handles teleportation logic for pocket dimensions.
 * Manages player teleportation in and out of pocket dimensions with visual effects.
 */
public final class PocketDimensionTeleporter {
    private PocketDimensionTeleporter() {}
    
    /**
     * Spawns teleportation particle effects at a position.
     */
    static void spawnTeleportationParticles(ServerLevel level, Vec3 position) {
        level.sendParticles(ParticleTypes.PORTAL,
            position.x, position.y, position.z,
            PocketDimensionConstants.PORTAL_PARTICLE_COUNT,
            PocketDimensionConstants.PORTAL_PARTICLE_SPREAD_X,
            PocketDimensionConstants.PORTAL_PARTICLE_SPREAD_Y,
            PocketDimensionConstants.PORTAL_PARTICLE_SPREAD_Z,
            PocketDimensionConstants.PORTAL_PARTICLE_SPEED);
        level.sendParticles(ParticleTypes.END_ROD,
            position.x, position.y, position.z,
            PocketDimensionConstants.END_ROD_PARTICLE_COUNT,
            PocketDimensionConstants.END_ROD_PARTICLE_SPREAD_X,
            PocketDimensionConstants.END_ROD_PARTICLE_SPREAD_Y,
            PocketDimensionConstants.END_ROD_PARTICLE_SPREAD_Z,
            PocketDimensionConstants.END_ROD_PARTICLE_SPEED);
    }
    
    /**
     * Teleports a player out of the pocket dimension to the target location.
     */
    public static void teleportPlayerOut(ServerPlayer player, ServerLevel pocketLevel, 
                                 ServerLevel targetLevel, BlockPos targetPos) {
        // Visual effect at origin (pocket dimension)
        Vec3 origin = player.position();
        spawnTeleportationParticles(pocketLevel, origin);
        
        pocketLevel.playSound(null, origin.x, origin.y, origin.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Find safe teleport position (above the target, on solid ground)
        BlockPos safePos = findSafeTeleportPosition(targetLevel, targetPos);
        
        // Teleport back
        player.teleportTo(targetLevel, safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5,
            java.util.Set.of(), player.getYRot(), player.getXRot(), false);
        
        // Visual effect at destination
        Vec3 dest = Vec3.atCenterOf(safePos);
        spawnTeleportationParticles(targetLevel, dest);
        
        targetLevel.playSound(null, dest.x, dest.y, dest.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
    
    /**
     * Finds a safe teleport position above the target position.
     */
    private static BlockPos findSafeTeleportPosition(ServerLevel level, BlockPos targetPos) {
        BlockPos safePos = targetPos;
        if (!level.getBlockState(targetPos).isAir() || 
            !level.getBlockState(targetPos.below()).canOcclude()) {
            // Find a safe spot above
            for (int y = 1; y <= PocketDimensionConstants.MAX_SAFE_TELEPORT_SEARCH_HEIGHT; y++) {
                BlockPos testPos = targetPos.offset(0, y, 0);
                if (level.getBlockState(testPos).isAir() && 
                    level.getBlockState(testPos.below()).canOcclude()) {
                    safePos = testPos;
                    break;
                }
            }
        }
        return safePos;
    }
}

