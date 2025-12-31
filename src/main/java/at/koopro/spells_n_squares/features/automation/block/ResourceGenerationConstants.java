package at.koopro.spells_n_squares.features.automation.block;

/**
 * Constants for resource generation system.
 * Centralizes magic numbers used across ResourceGeneratorBlock.
 */
public final class ResourceGenerationConstants {
    private ResourceGenerationConstants() {
        // Utility class - prevent instantiation
    }
    
    // Generation timing
    /** Interval between resource generation attempts (in ticks, 10 seconds at 20 TPS) */
    public static final int GENERATION_INTERVAL = 200;
    
    // Spawn chance
    /** Chance per tick to attempt generation (1% = 0.01f) */
    public static final float GENERATION_CHANCE = 0.01f;
    
    // Particle effects
    /** Number of particles to spawn when resource is generated */
    public static final int GENERATION_PARTICLE_COUNT = 20;
    /** Particle spread radius */
    public static final double PARTICLE_SPREAD = 0.3;
    /** Particle speed */
    public static final double PARTICLE_SPEED = 0.1;
    
    // Spawn position
    /** Vertical offset for spawning resource items (blocks above center) */
    public static final double SPAWN_Y_OFFSET = 1.0;
}


