package at.koopro.spells_n_squares.core.util;

/**
 * Constants for light block management.
 * Centralizes magic numbers used across light handlers.
 */
public final class LightConstants {
    private LightConstants() {
        // Utility class - prevent instantiation
    }
    
    // Light level constants
    public static final int MAX_LIGHT_LEVEL = 15; // Maximum light level
    
    // Light range constants
    public static final int LUMOS_LIGHT_RANGE = 8; // How far Lumos light reaches from player
    public static final int FLASHLIGHT_LIGHT_RANGE = 10; // How far flashlight light reaches
    
    // Update intervals
    public static final int LUMOS_UPDATE_INTERVAL = 5; // Update Lumos lights every N ticks
    public static final int FLASHLIGHT_TURN_THRESHOLD = 5; // Degrees of rotation before updating flashlight
    
    // Placement constraints
    public static final double MIN_DISTANCE_FROM_PLAYER = 1.5; // Minimum distance for Lumos lights
    public static final int MIN_DISTANCE_SQR = 2; // Minimum squared distance for flashlight lights
    public static final float LOOKING_DOWN_THRESHOLD = 20.0f; // Pitch angle threshold for "looking down"
}









