package at.koopro.spells_n_squares.core.fx;

import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.util.math.MathUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Helper class for accessing and calculating FX configuration values.
 */
public final class FXConfigHelper {
    private FXConfigHelper() {
    }
    
    /**
     * Gets the effective particle count multiplier based on quality preset.
     */
    public static double getEffectiveParticleMultiplier() {
        double baseMultiplier = Config.getParticleMultiplier();
        
        // Apply quality preset adjustments
        return switch (Config.getEffectQuality()) {
            case LOW -> baseMultiplier * 0.5;
            case MEDIUM -> baseMultiplier;
            case HIGH -> baseMultiplier * 1.5;
            case ULTRA -> baseMultiplier * 2.0;
        };
    }
    
    /**
     * Calculates the number of particles to spawn based on config.
     */
    public static int calculateParticleCount(int baseCount) {
        return (int) Math.max(0, Math.round(baseCount * getEffectiveParticleMultiplier()));
    }
    
    /**
     * Checks if particles should be rendered at the given distance.
     */
    public static boolean shouldRenderParticles(Player player, Vec3 position) {
        if (player == null || position == null) {
            return false;
        }
        
        double distance = MathUtils.distance(player.position(), position);
        return distance <= Config.getMaxParticleDistance();
    }
    
    /**
     * Gets the distance-based particle count multiplier (LOD).
     */
    public static double getDistanceMultiplier(Player player, Vec3 position) {
        if (player == null || position == null) {
            return 0.0;
        }
        
        double distance = MathUtils.distance(player.position(), position);
        int maxDistance = Config.getMaxParticleDistance();
        
        if (distance > maxDistance) {
            return 0.0;
        }
        
        // Reduce particles at distance (linear falloff)
        double distanceRatio = distance / maxDistance;
        return 1.0 - (distanceRatio * 0.5); // 50% reduction at max distance
    }
    
    /**
     * Calculates particle count with distance-based LOD.
     */
    public static int calculateParticleCountWithLOD(Player player, Vec3 position, int baseCount) {
        double distanceMultiplier = getDistanceMultiplier(player, position);
        double qualityMultiplier = getEffectiveParticleMultiplier();
        return (int) Math.max(0, Math.round(baseCount * qualityMultiplier * distanceMultiplier));
    }
    
    /**
     * Gets the screen effect intensity multiplier.
     */
    public static float getScreenEffectIntensity() {
        return (float) Config.getScreenEffectIntensity();
    }
    
    /**
     * Checks if high-quality effects should be used.
     */
    public static boolean useHighQualityEffects() {
        return Config.getEffectQuality() == Config.EffectQuality.HIGH || 
               Config.getEffectQuality() == Config.EffectQuality.ULTRA;
    }
    
    /**
     * Checks if a particle position is likely off-screen based on distance and angle.
     * This is a simple heuristic - full frustum culling would require camera data.
     * 
     * @param player The player to check visibility relative to
     * @param position The particle position
     * @return true if particle should be culled (not rendered)
     */
    public static boolean shouldCullParticle(Player player, Vec3 position) {
        if (player == null || position == null) {
            return true; // Cull if invalid
        }
        
        // Distance-based culling (already handled by shouldRenderParticles, but double-check)
        double distance = MathUtils.distance(player.position(), position);
        if (distance > Config.getMaxParticleDistance()) {
            return true;
        }
        
        // For very far particles, use more aggressive culling
        // At 80% of max distance, start reducing particle count more aggressively
        double maxDistance = Config.getMaxParticleDistance();
        if (distance > maxDistance * 0.8) {
            // Random culling for far particles (50% chance)
            return Math.random() > 0.5;
        }
        
        return false;
    }
}
