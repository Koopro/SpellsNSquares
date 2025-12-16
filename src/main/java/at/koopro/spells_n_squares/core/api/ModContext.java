package at.koopro.spells_n_squares.core.api;

/**
 * Centralized context for accessing feature APIs.
 * Provides a service locator pattern for feature access, enabling dependency injection for testing.
 */
public final class ModContext {
    private ModContext() {
        // Utility class - prevent instantiation
    }
    
    // Feature API instances
    private static ISpellManager spellManager = SpellManagerWrapper.INSTANCE;
    private static IPlayerClassManager playerClassManager = PlayerClassManagerWrapper.INSTANCE;
    private static ISpellRegistry spellRegistry = SpellRegistryWrapper.INSTANCE;
    
    /**
     * Gets the spell manager instance.
     * @return The spell manager
     */
    public static ISpellManager getSpellManager() {
        return spellManager;
    }
    
    /**
     * Gets the player class manager instance.
     * @return The player class manager
     */
    public static IPlayerClassManager getPlayerClassManager() {
        return playerClassManager;
    }
    
    /**
     * Gets the spell registry instance.
     * @return The spell registry
     */
    public static ISpellRegistry getSpellRegistry() {
        return spellRegistry;
    }
    
    /**
     * Sets the spell manager instance (for dependency injection/testing).
     * @param manager The spell manager to use
     */
    public static void setSpellManager(ISpellManager manager) {
        spellManager = manager;
    }
    
    /**
     * Sets the player class manager instance (for dependency injection/testing).
     * @param manager The player class manager to use
     */
    public static void setPlayerClassManager(IPlayerClassManager manager) {
        playerClassManager = manager;
    }
    
    /**
     * Sets the spell registry instance (for dependency injection/testing).
     * @param registry The spell registry to use
     */
    public static void setSpellRegistry(ISpellRegistry registry) {
        spellRegistry = registry;
    }
    
    /**
     * Resets all managers to their default implementations.
     * Useful for testing or resetting state.
     */
    public static void resetToDefaults() {
        spellManager = SpellManagerWrapper.INSTANCE;
        playerClassManager = PlayerClassManagerWrapper.INSTANCE;
        spellRegistry = SpellRegistryWrapper.INSTANCE;
    }
}

