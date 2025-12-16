package at.koopro.spells_n_squares.core.fx;

import at.koopro.spells_n_squares.core.config.Config;
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
        
        double distance = player.position().distanceTo(position);
        return distance <= Config.getMaxParticleDistance();
    }
    
    /**
     * Gets the distance-based particle count multiplier (LOD).
     */
    public static double getDistanceMultiplier(Player player, Vec3 position) {
        if (player == null || position == null) {
            return 0.0;
        }
        
        double distance = player.position().distanceTo(position);
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
}
