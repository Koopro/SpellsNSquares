package at.koopro.spells_n_squares.features.storage;

/**
 * Constants for pocket dimension management.
 * Centralizes magic numbers used across PocketDimensionManager.
 */
public final class PocketDimensionConstants {
    private PocketDimensionConstants() {
        // Utility class - prevent instantiation
    }
    
    // Particle effect parameters
    /** Number of portal particles to spawn for teleportation effects */
    public static final int PORTAL_PARTICLE_COUNT = 30;
    /** Portal particle spread on X axis */
    public static final double PORTAL_PARTICLE_SPREAD_X = 0.5;
    /** Portal particle spread on Y axis */
    public static final double PORTAL_PARTICLE_SPREAD_Y = 0.5;
    /** Portal particle spread on Z axis */
    public static final double PORTAL_PARTICLE_SPREAD_Z = 0.5;
    /** Portal particle speed */
    public static final double PORTAL_PARTICLE_SPEED = 0.1;
    
    /** Number of end rod particles to spawn for teleportation effects */
    public static final int END_ROD_PARTICLE_COUNT = 20;
    /** End rod particle spread on X axis */
    public static final double END_ROD_PARTICLE_SPREAD_X = 0.3;
    /** End rod particle spread on Y axis */
    public static final double END_ROD_PARTICLE_SPREAD_Y = 0.3;
    /** End rod particle spread on Z axis */
    public static final double END_ROD_PARTICLE_SPREAD_Z = 0.3;
    /** End rod particle speed */
    public static final double END_ROD_PARTICLE_SPEED = 0.05;
    
    // Grid and spacing constants
    /** Grid size for spreading pocket dimensions (1000x1000 grid) */
    public static final int GRID_SIZE = 1000;
    /** Spacing between pocket dimension items in the grid */
    public static final int GRID_SPACING_OFFSET = 10;
    
    // Spawn and structure constants
    /** Standard spawn height for pocket dimensions */
    public static final int STANDARD_SPAWN_HEIGHT = 64;
    /** Base size for pocket dimensions */
    public static final int BASE_DIMENSION_SIZE = 32;
    /** Size increase per upgrade level */
    public static final int SIZE_INCREASE_PER_UPGRADE = 8;
    
    // Structure generation constants
    /** Base floor size for Newt's Case structure */
    public static final int BASE_FLOOR_SIZE = 5;
    /** Floor size increase per upgrade level */
    public static final int FLOOR_SIZE_INCREASE_PER_UPGRADE = 2;
    /** Maximum floor size for Newt's Case structure */
    public static final int MAX_FLOOR_SIZE = 11;
    /** Wall height for Newt's Case structure */
    public static final int WALL_HEIGHT = 3;
    /** Exit platform height above spawn (blocks above spawn) */
    public static final int EXIT_PLATFORM_HEIGHT = 5;
    /** Maximum search height for safe teleport position */
    public static final int MAX_SAFE_TELEPORT_SEARCH_HEIGHT = 5;
}


