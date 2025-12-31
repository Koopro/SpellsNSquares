package at.koopro.spells_n_squares.features.environment;

/**
 * Constants for creature migration system.
 * Centralizes magic numbers used across CreatureMigrationHandler.
 */
public final class CreatureMigrationConstants {
    private CreatureMigrationConstants() {
        // Utility class - prevent instantiation
    }
    
    // Migration spawn chance
    /** Chance of migration spawning per tick (1 in N = very rare) */
    public static final int MIGRATION_SPAWN_CHANCE = 50000;
    
    // Migration duration
    /** Migration duration in ticks (10 minutes at 20 TPS) */
    public static final int MIGRATION_DURATION = 12000;
    
    // Spawn parameters
    /** Maximum distance from player for creature spawn (in blocks) */
    public static final double MAX_SPAWN_DISTANCE = 30.0;
    /** Chance per player to spawn creatures near them (0.0 to 1.0) */
    public static final float SPAWN_NEAR_PLAYER_CHANCE = 0.5f;
    
    // Sound parameters
    /** Sound Y coordinate (height) for migration announcement */
    public static final double MIGRATION_SOUND_Y = 200.0;
    /** Sound volume for migration announcement */
    public static final float MIGRATION_SOUND_VOLUME = 0.3f;
    /** Sound pitch for migration announcement */
    public static final float MIGRATION_SOUND_PITCH = 1.0f;
    
    // Particle effects
    /** Number of particles to spawn when creature appears */
    public static final int CREATURE_SPAWN_PARTICLE_COUNT = 20;
    /** Particle spread radius */
    public static final double PARTICLE_SPREAD = 1.0;
    /** Particle speed */
    public static final double PARTICLE_SPEED = 0.1;
    
    // Additional spawn timing
    /** Interval between additional creature spawns during migration (in ticks) */
    public static final int ADDITIONAL_SPAWN_INTERVAL = 2000;
    /** Chance for additional spawns during migration (0.0 to 1.0) */
    public static final float ADDITIONAL_SPAWN_CHANCE = 0.3f;
}

