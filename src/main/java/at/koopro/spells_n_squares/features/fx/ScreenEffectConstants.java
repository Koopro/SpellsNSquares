package at.koopro.spells_n_squares.features.fx;

/**
 * Constants for screen effect system.
 * Centralizes magic numbers used across ScreenEffectManager.
 */
public final class ScreenEffectConstants {
    private ScreenEffectConstants() {
        // Utility class - prevent instantiation
    }
    
    // Shake frequency constants
    /** Base frequency multiplier for X-axis shake */
    public static final float SHAKE_FREQUENCY_X = 0.3f;
    /** Base frequency multiplier for Y-axis shake */
    public static final float SHAKE_FREQUENCY_Y = 0.5f;
    
    // Shake wave parameters
    /** Primary frequency for shake waves */
    public static final double SHAKE_WAVE_PRIMARY = 1.0;
    /** Secondary frequency for X-axis shake waves */
    public static final double SHAKE_WAVE_SECONDARY_X = 2.3;
    /** Secondary frequency for Y-axis shake waves */
    public static final double SHAKE_WAVE_SECONDARY_Y = 1.7;
    
    // Shake amplitude weights
    /** Weight for primary frequency component */
    public static final double SHAKE_AMPLITUDE_PRIMARY = 0.6;
    /** Weight for secondary frequency component */
    public static final double SHAKE_AMPLITUDE_SECONDARY = 0.4;
    
    // Shake intensity multiplier
    /** Multiplier to convert intensity to pixel offset */
    public static final double SHAKE_INTENSITY_MULTIPLIER = 10.0;
    
    // Interpolation parameters
    /** Interpolation factor for smooth shake motion (0.0 to 1.0) */
    public static final float SHAKE_INTERPOLATION_FACTOR = 0.3f;
    
    // Camera shake parameters
    /** Scale factor to convert pixel offset to camera angle offset */
    public static final float CAMERA_SHAKE_SCALE = 0.1f;
    /** Minimum shake offset squared length to apply camera shake */
    public static final double MIN_SHAKE_LENGTH_SQR = 0.001;
}


