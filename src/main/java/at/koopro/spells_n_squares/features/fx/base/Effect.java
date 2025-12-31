package at.koopro.spells_n_squares.features.fx.base;

/**
 * Base interface for all effects (shader, screen, particle, etc.).
 * Provides a unified lifecycle for effect management.
 */
public interface Effect {
    /**
     * Gets the unique identifier for this effect.
     * @return The effect identifier
     */
    String getId();
    
    /**
     * Gets the intensity of the effect (0.0 to 1.0).
     * @return The intensity
     */
    float getIntensity();
    
    /**
     * Gets the duration of the effect in ticks.
     * @return The duration, or -1 for infinite/persistent
     */
    int getDuration();
    
    /**
     * Gets the current age of the effect in ticks.
     * @return The age
     */
    int getAge();
    
    /**
     * Updates the effect (called each tick).
     */
    void tick();
    
    /**
     * Checks if the effect has expired.
     * @return true if expired, false otherwise
     */
    boolean isExpired();
    
    /**
     * Gets the current intensity (may fade over time).
     * @return The current intensity
     */
    float getCurrentIntensity();
    
    /**
     * Stops the effect immediately.
     */
    void stop();
}


