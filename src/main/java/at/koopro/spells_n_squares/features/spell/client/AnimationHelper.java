package at.koopro.spells_n_squares.features.spell.client;

import net.minecraft.util.Mth;

/**
 * Helper class for smooth animations in GUI screens.
 * Provides interpolation and easing functions for animations.
 */
public final class AnimationHelper {
    private AnimationHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Easing function types for animations.
     */
    public enum Easing {
        LINEAR,
        EASE_IN,
        EASE_OUT,
        EASE_IN_OUT,
        SMOOTH
    }
    
    /**
     * Interpolates between two values using linear interpolation.
     * 
     * @param start Start value
     * @param end End value
     * @param progress Progress (0.0 to 1.0)
     * @return Interpolated value
     */
    public static float lerp(float start, float end, float progress) {
        return start + (end - start) * Mth.clamp(progress, 0.0f, 1.0f);
    }
    
    /**
     * Interpolates between two values using linear interpolation.
     * 
     * @param start Start value
     * @param end End value
     * @param progress Progress (0.0 to 1.0)
     * @return Interpolated value
     */
    public static double lerp(double start, double end, double progress) {
        return start + (end - start) * Mth.clamp((float) progress, 0.0f, 1.0f);
    }
    
    /**
     * Interpolates between two values using linear interpolation.
     * 
     * @param start Start value
     * @param end End value
     * @param progress Progress (0.0 to 1.0)
     * @return Interpolated value
     */
    public static int lerp(int start, int end, float progress) {
        return Math.round(start + (end - start) * Mth.clamp(progress, 0.0f, 1.0f));
    }
    
    /**
     * Applies easing to a progress value (0.0 to 1.0).
     * 
     * @param progress Progress value (0.0 to 1.0)
     * @param easing Easing function to apply
     * @return Eased progress value
     */
    public static float applyEasing(float progress, Easing easing) {
        progress = Mth.clamp(progress, 0.0f, 1.0f);
        return switch (easing) {
            case LINEAR -> progress;
            case EASE_IN -> progress * progress;
            case EASE_OUT -> 1.0f - (1.0f - progress) * (1.0f - progress);
            case EASE_IN_OUT -> progress < 0.5f
                ? 2.0f * progress * progress
                : 1.0f - 2.0f * (1.0f - progress) * (1.0f - progress);
            case SMOOTH -> progress * progress * (3.0f - 2.0f * progress); // Smoothstep
        };
    }
    
    /**
     * Interpolates between two values with easing.
     * 
     * @param start Start value
     * @param end End value
     * @param progress Progress (0.0 to 1.0)
     * @param easing Easing function to apply
     * @return Interpolated value
     */
    public static float lerpEased(float start, float end, float progress, Easing easing) {
        return lerp(start, end, applyEasing(progress, easing));
    }
    
    /**
     * Interpolates between two color values (ARGB format).
     * 
     * @param color1 First color (ARGB)
     * @param color2 Second color (ARGB)
     * @param progress Progress (0.0 to 1.0)
     * @return Interpolated color (ARGB)
     */
    public static int lerpColor(int color1, int color2, float progress) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int a = lerp(a1, a2, progress);
        int r = lerp(r1, r2, progress);
        int g = lerp(g1, g2, progress);
        int b = lerp(b1, b2, progress);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    /**
     * Interpolates between two color values with easing.
     * 
     * @param color1 First color (ARGB)
     * @param color2 Second color (ARGB)
     * @param progress Progress (0.0 to 1.0)
     * @param easing Easing function to apply
     * @return Interpolated color (ARGB)
     */
    public static int lerpColorEased(int color1, int color2, float progress, Easing easing) {
        return lerpColor(color1, color2, applyEasing(progress, easing));
    }
    
    /**
     * Calculates fade-in progress based on time.
     * 
     * @param startTime Start time (in ticks or milliseconds)
     * @param duration Duration (in ticks or milliseconds)
     * @param currentTime Current time (in ticks or milliseconds)
     * @return Progress (0.0 to 1.0)
     */
    public static float fadeInProgress(long startTime, long duration, long currentTime) {
        if (currentTime < startTime) {
            return 0.0f;
        }
        long elapsed = currentTime - startTime;
        if (elapsed >= duration) {
            return 1.0f;
        }
        return (float) elapsed / (float) duration;
    }
    
    /**
     * Calculates fade-out progress based on time.
     * 
     * @param startTime Start time (in ticks or milliseconds)
     * @param duration Duration (in ticks or milliseconds)
     * @param currentTime Current time (in ticks or milliseconds)
     * @return Progress (1.0 to 0.0)
     */
    public static float fadeOutProgress(long startTime, long duration, long currentTime) {
        return 1.0f - fadeInProgress(startTime, duration, currentTime);
    }
}


