package at.koopro.spells_n_squares.core.util.math;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Utility class for common mathematical operations and calculations.
 * Provides helper methods for clamping, interpolation, distance calculations, and more.
 */
public final class MathUtils {
    private MathUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Clamps a value between min and max (inclusive).
     * 
     * @param value The value to clamp
     * @param min The minimum value
     * @param max The maximum value
     * @return The clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Mth.clamp(value, min, max);
    }
    
    /**
     * Clamps a value between min and max (inclusive).
     * 
     * @param value The value to clamp
     * @param min The minimum value
     * @param max The maximum value
     * @return The clamped value
     */
    public static double clamp(double value, double min, double max) {
        return Mth.clamp(value, min, max);
    }
    
    /**
     * Clamps a value between min and max (inclusive).
     * 
     * @param value The value to clamp
     * @param min The minimum value
     * @param max The maximum value
     * @return The clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Mth.clamp(value, min, max);
    }
    
    /**
     * Linear interpolation between two values.
     * 
     * @param start The start value
     * @param end The end value
     * @param t The interpolation factor (0.0 = start, 1.0 = end)
     * @return The interpolated value
     */
    public static float lerp(float start, float end, float t) {
        return Mth.lerp(t, start, end);
    }
    
    /**
     * Linear interpolation between two values.
     * 
     * @param start The start value
     * @param end The end value
     * @param t The interpolation factor (0.0 = start, 1.0 = end)
     * @return The interpolated value
     */
    public static double lerp(double start, double end, double t) {
        return Mth.lerp(t, start, end);
    }
    
    /**
     * Smooth interpolation (slerp) using smoothstep function.
     * 
     * @param start The start value
     * @param end The end value
     * @param t The interpolation factor (0.0 = start, 1.0 = end)
     * @return The smoothly interpolated value
     */
    public static float slerp(float start, float end, float t) {
        float smoothT = t * t * (3.0f - 2.0f * t); // Smoothstep
        return lerp(start, end, smoothT);
    }
    
    /**
     * Smooth interpolation (slerp) using smoothstep function.
     * 
     * @param start The start value
     * @param end The end value
     * @param t The interpolation factor (0.0 = start, 1.0 = end)
     * @return The smoothly interpolated value
     */
    public static double slerp(double start, double end, double t) {
        double smoothT = t * t * (3.0 - 2.0 * t); // Smoothstep
        return lerp(start, end, smoothT);
    }
    
    /**
     * Calculates the distance between two 2D points.
     * 
     * @param x1 First point X
     * @param y1 First point Y
     * @param x2 Second point X
     * @param y2 Second point Y
     * @return The distance
     */
    public static double distance2D(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Calculates the distance between two 3D points.
     * 
     * @param x1 First point X
     * @param y1 First point Y
     * @param z1 First point Z
     * @param x2 Second point X
     * @param y2 Second point Y
     * @param z2 Second point Z
     * @return The distance
     */
    public static double distance3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Calculates the squared distance between two 3D points (faster, no sqrt).
     * 
     * @param x1 First point X
     * @param y1 First point Y
     * @param z1 First point Z
     * @param x2 Second point X
     * @param y2 Second point Y
     * @param z2 Second point Z
     * @return The squared distance
     */
    public static double distanceSquared3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return dx * dx + dy * dy + dz * dz;
    }
    
    /**
     * Converts degrees to radians.
     * 
     * @param degrees The angle in degrees
     * @return The angle in radians
     */
    public static float toRadians(float degrees) {
        return (float) Math.toRadians(degrees);
    }
    
    /**
     * Converts degrees to radians.
     * 
     * @param degrees The angle in degrees
     * @return The angle in radians
     */
    public static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }
    
    /**
     * Converts radians to degrees.
     * 
     * @param radians The angle in radians
     * @return The angle in degrees
     */
    public static float toDegrees(float radians) {
        return (float) Math.toDegrees(radians);
    }
    
    /**
     * Converts radians to degrees.
     * 
     * @param radians The angle in radians
     * @return The angle in degrees
     */
    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }
    
    /**
     * Calculates a percentage value.
     * 
     * @param part The part value
     * @param total The total value
     * @return The percentage (0.0 to 1.0)
     */
    public static float percentage(float part, float total) {
        if (total == 0.0f) {
            return 0.0f;
        }
        return part / total;
    }
    
    /**
     * Calculates a percentage value.
     * 
     * @param part The part value
     * @param total The total value
     * @return The percentage (0.0 to 1.0)
     */
    public static double percentage(double part, double total) {
        if (total == 0.0) {
            return 0.0;
        }
        return part / total;
    }
    
    /**
     * Rounds a float to the nearest integer.
     * 
     * @param value The value to round
     * @return The rounded integer
     */
    public static int round(float value) {
        return Math.round(value);
    }
    
    /**
     * Rounds a double to the nearest integer.
     * 
     * @param value The value to round
     * @return The rounded integer
     */
    public static long round(double value) {
        return Math.round(value);
    }
    
    /**
     * Rounds a float to a specific number of decimal places.
     * 
     * @param value The value to round
     * @param decimals The number of decimal places
     * @return The rounded value
     */
    public static float round(float value, int decimals) {
        float factor = (float) Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }
    
    /**
     * Rounds a double to a specific number of decimal places.
     * 
     * @param value The value to round
     * @param decimals The number of decimal places
     * @return The rounded value
     */
    public static double round(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }
    
    /**
     * Calculates the distance between two Vec3 positions.
     * 
     * @param pos1 First position
     * @param pos2 Second position
     * @return The distance
     */
    public static double distance(Vec3 pos1, Vec3 pos2) {
        if (pos1 == null || pos2 == null) {
            return 0.0;
        }
        return pos1.distanceTo(pos2);
    }
    
    /**
     * Calculates the squared distance between two Vec3 positions (faster, no sqrt).
     * 
     * @param pos1 First position
     * @param pos2 Second position
     * @return The squared distance
     */
    public static double distanceSquared(Vec3 pos1, Vec3 pos2) {
        if (pos1 == null || pos2 == null) {
            return 0.0;
        }
        return pos1.distanceToSqr(pos2);
    }
    
    /**
     * Maps a value from one range to another.
     * 
     * @param value The value to map
     * @param inMin Input range minimum
     * @param inMax Input range maximum
     * @param outMin Output range minimum
     * @param outMax Output range maximum
     * @return The mapped value
     */
    public static float mapRange(float value, float inMin, float inMax, float outMin, float outMax) {
        float t = (value - inMin) / (inMax - inMin);
        return lerp(outMin, outMax, t);
    }
    
    /**
     * Maps a value from one range to another.
     * 
     * @param value The value to map
     * @param inMin Input range minimum
     * @param inMax Input range maximum
     * @param outMin Output range minimum
     * @param outMax Output range maximum
     * @return The mapped value
     */
    public static double mapRange(double value, double inMin, double inMax, double outMin, double outMax) {
        double t = (value - inMin) / (inMax - inMin);
        return lerp(outMin, outMax, t);
    }
    
    /**
     * Wraps an angle to the range [0, 360) degrees.
     * 
     * @param angle The angle in degrees
     * @return The wrapped angle
     */
    public static float wrapAngle(float angle) {
        return Mth.wrapDegrees(angle);
    }
    
    /**
     * Wraps an angle to the range [0, 360) degrees.
     * 
     * @param angle The angle in degrees
     * @return The wrapped angle
     */
    public static double wrapAngle(double angle) {
        return Mth.wrapDegrees(angle);
    }
}


