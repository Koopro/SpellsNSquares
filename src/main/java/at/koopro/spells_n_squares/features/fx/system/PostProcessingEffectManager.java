package at.koopro.spells_n_squares.features.fx.system;

import at.koopro.spells_n_squares.core.config.Config;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the lifecycle of post-processing effects.
 * Handles adding, removing, ticking, and querying active effects.
 */
final class PostProcessingEffectManager {
    // Active post-processing effects
    private static final List<PostProcessingEffect> activeEffects = new ArrayList<>();
    
    private PostProcessingEffectManager() {}
    
    /**
     * Represents a single post-processing effect with duration and intensity.
     */
    static class PostProcessingEffect {
        final Identifier shaderId;
        final float intensity;
        final int duration; // -1 for infinite/persistent
        int age;
        
        PostProcessingEffect(Identifier shaderId, float intensity, int duration) {
            this.shaderId = shaderId;
            this.intensity = intensity;
            this.duration = duration;
            this.age = 0;
        }
        
        void tick() {
            age++;
        }
        
        boolean isExpired() {
            return duration >= 0 && age >= duration;
        }
        
        float getCurrentIntensity() {
            // For infinite duration, return full intensity
            if (duration < 0) {
                return intensity;
            }
            // Fade out over time
            float progress = (float) age / duration;
            return intensity * (1.0f - progress);
        }
    }
    
    /**
     * Adds a new post-processing effect.
     * 
     * @param shaderId The shader identifier
     * @param intensity The intensity of the effect (0.0 to 1.0)
     * @param duration Duration in ticks (-1 for infinite/persistent)
     */
    static void addEffect(Identifier shaderId, float intensity, int duration) {
        if (Config.getScreenEffectIntensity() <= 0.0) {
            return;
        }
        
        float adjustedIntensity = (float) (intensity * Config.getScreenEffectIntensity());
        activeEffects.add(new PostProcessingEffect(shaderId, adjustedIntensity, duration));
    }
    
    /**
     * Adds a persistent post-processing effect (no duration, stays until manually removed).
     * 
     * @param shaderId The shader identifier
     * @param intensity The intensity (0.0 to 1.0)
     */
    static void addPersistentEffect(Identifier shaderId, float intensity) {
        addEffect(shaderId, intensity, -1);
    }
    
    /**
     * Removes all active effects for the given shader.
     * 
     * @param shaderId The shader identifier
     * @return true if any effects were removed
     */
    static boolean removeEffect(Identifier shaderId) {
        return activeEffects.removeIf(effect -> effect.shaderId.equals(shaderId));
    }
    
    /**
     * Checks if an effect is currently active for the given shader.
     * 
     * @param shaderId The shader identifier
     * @return true if the effect is active
     */
    static boolean isEffectActive(Identifier shaderId) {
        return activeEffects.stream().anyMatch(effect -> 
            effect.shaderId.equals(shaderId) && !effect.isExpired());
    }
    
    /**
     * Checks if a persistent effect is currently active for the given shader.
     * 
     * @param shaderId The shader identifier
     * @return true if a persistent effect is active
     */
    static boolean hasPersistentEffect(Identifier shaderId) {
        return activeEffects.stream().anyMatch(effect -> 
            effect.shaderId.equals(shaderId) && effect.duration < 0 && !effect.isExpired());
    }
    
    /**
     * Ticks all active effects and removes expired ones.
     */
    static void tickEffects() {
        activeEffects.removeIf(effect -> {
            effect.tick();
            return effect.isExpired();
        });
    }
    
    /**
     * Gets all active effects.
     * 
     * @return A copy of the active effects list
     */
    static List<PostProcessingEffect> getActiveEffects() {
        return new ArrayList<>(activeEffects);
    }
    
    /**
     * Clears all active post-processing effects.
     */
    static void clearAllEffects() {
        activeEffects.clear();
    }
    
    /**
     * Checks if there are any active effects.
     * 
     * @return true if there are active effects
     */
    static boolean hasActiveEffects() {
        return !activeEffects.isEmpty();
    }
}

