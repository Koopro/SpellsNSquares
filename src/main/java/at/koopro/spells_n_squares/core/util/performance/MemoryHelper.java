package at.koopro.spells_n_squares.core.util.performance;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;

/**
 * Memory usage tracking and optimization utility.
 * Provides methods for monitoring memory usage.
 */
public final class MemoryHelper {
    
    private MemoryHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets the current memory usage in bytes.
     * 
     * @return Current memory usage
     */
    public static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    /**
     * Gets the total allocated memory in bytes.
     * 
     * @return Total allocated memory
     */
    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }
    
    /**
     * Gets the maximum available memory in bytes.
     * 
     * @return Maximum memory
     */
    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }
    
    /**
     * Gets the free memory in bytes.
     * 
     * @return Free memory
     */
    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
    
    /**
     * Gets memory usage as a percentage (0.0 to 1.0).
     * 
     * @return Memory usage percentage
     */
    public static double getMemoryUsagePercent() {
        long max = getMaxMemory();
        if (max == 0) {
            return 0.0;
        }
        return (double) getUsedMemory() / max;
    }
    
    /**
     * Suggests a garbage collection.
     * Note: This is just a hint to the JVM.
     */
    public static void suggestGC() {
        System.gc();
    }
    
    /**
     * Logs current memory usage.
     */
    public static void logMemoryUsage() {
        long used = getUsedMemory();
        long total = getTotalMemory();
        long max = getMaxMemory();
        double percent = getMemoryUsagePercent() * 100.0;
        
        DevLogger.logDebug(MemoryHelper.class, "logMemoryUsage",
            String.format("Memory: used=%.2fMB, total=%.2fMB, max=%.2fMB, usage=%.1f%%",
                used / (1024.0 * 1024.0),
                total / (1024.0 * 1024.0),
                max / (1024.0 * 1024.0),
                percent));
    }
}

