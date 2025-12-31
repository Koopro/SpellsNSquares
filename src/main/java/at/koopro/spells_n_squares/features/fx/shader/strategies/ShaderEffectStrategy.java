package at.koopro.spells_n_squares.features.fx.shader.strategies;

/**
 * Strategy interface for shader effects.
 * Each shader effect type implements this interface to handle its specific behavior.
 */
public interface ShaderEffectStrategy {
    /**
     * Triggers the shader effect with the given intensity.
     * @param intensity The intensity of the effect (0.0 to 1.0)
     */
    void trigger(float intensity);
    
    /**
     * Gets the primary name for this effect (used for mapping).
     * @return The primary effect name
     */
    String getPrimaryName();
    
    /**
     * Gets all aliases for this effect (alternative names that map to this effect).
     * @return Array of alias names, or empty array if no aliases
     */
    default String[] getAliases() {
        return new String[0];
    }
}

