package at.koopro.spells_n_squares.core.util.time;

import net.minecraft.util.Mth;

/**
 * Utility class for time-related operations.
 * Provides time conversion, formatting, and game time utilities.
 */
public final class TimeUtils {
    
    private TimeUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Converts ticks to seconds.
     * 
     * @param ticks The number of ticks
     * @return The number of seconds
     */
    public static double ticksToSeconds(int ticks) {
        return ticks / 20.0;
    }
    
    /**
     * Converts seconds to ticks.
     * 
     * @param seconds The number of seconds
     * @return The number of ticks
     */
    public static int secondsToTicks(double seconds) {
        return (int) Math.round(seconds * 20.0);
    }
    
    /**
     * Converts ticks to milliseconds.
     * 
     * @param ticks The number of ticks
     * @return The number of milliseconds
     */
    public static long ticksToMilliseconds(int ticks) {
        return (long) (ticks * 50.0);
    }
    
    /**
     * Converts milliseconds to ticks.
     * 
     * @param milliseconds The number of milliseconds
     * @return The number of ticks
     */
    public static int millisecondsToTicks(long milliseconds) {
        return (int) Math.round(milliseconds / 50.0);
    }
    
    /**
     * Formats ticks as a human-readable time string.
     * Examples: "5s", "2m 30s", "1h 15m"
     * 
     * @param ticks The number of ticks
     * @return Formatted time string
     */
    public static String formatTicks(int ticks) {
        if (ticks < 0) {
            return "0s";
        }
        
        int totalSeconds = (int) Math.floor(ticksToSeconds(ticks));
        
        if (totalSeconds < 60) {
            return totalSeconds + "s";
        }
        
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        
        if (minutes < 60) {
            if (seconds > 0) {
                return minutes + "m " + seconds + "s";
            }
            return minutes + "m";
        }
        
        int hours = minutes / 60;
        minutes = minutes % 60;
        
        if (hours < 24) {
            if (minutes > 0) {
                return hours + "h " + minutes + "m";
            }
            return hours + "h";
        }
        
        int days = hours / 24;
        hours = hours % 24;
        
        if (days < 7) {
            if (hours > 0) {
                return days + "d " + hours + "h";
            }
            return days + "d";
        }
        
        return days + "d";
    }
    
    /**
     * Formats milliseconds as a human-readable time string.
     * 
     * @param milliseconds The number of milliseconds
     * @return Formatted time string
     */
    public static String formatMilliseconds(long milliseconds) {
        return formatTicks(millisecondsToTicks(milliseconds));
    }
    
    /**
     * Gets the current game time in ticks from a level.
     * 
     * @param level The level (can be null)
     * @return Current game time in ticks, or 0 if level is null
     */
    public static long getGameTime(net.minecraft.world.level.Level level) {
        if (level == null) {
            return 0;
        }
        return level.getGameTime();
    }
    
    /**
     * Gets the current game time in seconds from a level.
     * 
     * @param level The level (can be null)
     * @return Current game time in seconds, or 0 if level is null
     */
    public static double getGameTimeSeconds(net.minecraft.world.level.Level level) {
        return ticksToSeconds((int) getGameTime(level));
    }
    
    /**
     * Checks if a time has elapsed since a start time.
     * 
     * @param startTime The start time (in ticks)
     * @param duration The duration to check (in ticks)
     * @param currentTime The current time (in ticks)
     * @return true if the duration has elapsed
     */
    public static boolean hasElapsed(long startTime, long duration, long currentTime) {
        return (currentTime - startTime) >= duration;
    }
    
    /**
     * Gets the remaining time until a duration elapses.
     * 
     * @param startTime The start time (in ticks)
     * @param duration The duration (in ticks)
     * @param currentTime The current time (in ticks)
     * @return Remaining time in ticks, or 0 if elapsed
     */
    public static long getRemainingTime(long startTime, long duration, long currentTime) {
        long elapsed = currentTime - startTime;
        return Math.max(0, duration - elapsed);
    }
    
    /**
     * Clamps a time value between min and max.
     * 
     * @param time The time value (in ticks)
     * @param min The minimum time (in ticks)
     * @param max The maximum time (in ticks)
     * @return Clamped time value
     */
    public static long clampTime(long time, long min, long max) {
        return Mth.clamp(time, min, max);
    }
    
    /**
     * Interpolates between two time values.
     * 
     * @param start The start time (in ticks)
     * @param end The end time (in ticks)
     * @param t The interpolation factor (0.0 to 1.0)
     * @return Interpolated time value
     */
    public static long lerpTime(long start, long end, float t) {
        return (long) Mth.lerp(t, (double) start, (double) end);
    }
}
