package at.koopro.spells_n_squares.core.util.performance;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Performance profiling and timing utility.
 * Provides methods for profiling method execution times and performance metrics.
 */
public final class PerformanceProfiler {
    
    private static final Map<String, ProfileData> PROFILES = new ConcurrentHashMap<>();
    private static final Map<String, Long> ACTIVE_PROFILES = new ConcurrentHashMap<>();
    
    private PerformanceProfiler() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents profile data for a method/operation.
     */
    public static class ProfileData {
        private long totalTime;
        private long callCount;
        private long minTime;
        private long maxTime;
        
        public ProfileData() {
            this.minTime = Long.MAX_VALUE;
            this.maxTime = 0;
        }
        
        public void recordCall(long duration) {
            totalTime += duration;
            callCount++;
            minTime = Math.min(minTime, duration);
            maxTime = Math.max(maxTime, duration);
        }
        
        public long getTotalTime() {
            return totalTime;
        }
        
        public long getCallCount() {
            return callCount;
        }
        
        public double getAverageTime() {
            return callCount > 0 ? (double) totalTime / callCount : 0.0;
        }
        
        public long getMinTime() {
            return minTime == Long.MAX_VALUE ? 0 : minTime;
        }
        
        public long getMaxTime() {
            return maxTime;
        }
    }
    
    /**
     * Starts profiling an operation.
     * 
     * @param name The operation name
     */
    public static void start(String name) {
        if (name == null) {
            return;
        }
        
        ACTIVE_PROFILES.put(name, System.nanoTime());
    }
    
    /**
     * Stops profiling an operation and records the duration.
     * 
     * @param name The operation name
     */
    public static void stop(String name) {
        if (name == null) {
            return;
        }
        
        Long startTime = ACTIVE_PROFILES.remove(name);
        if (startTime != null) {
            long duration = System.nanoTime() - startTime;
            PROFILES.computeIfAbsent(name, k -> new ProfileData()).recordCall(duration);
        }
    }
    
    /**
     * Profiles a runnable operation.
     * 
     * @param name The operation name
     * @param operation The operation to profile
     */
    public static void profile(String name, Runnable operation) {
        if (name == null || operation == null) {
            return;
        }
        
        start(name);
        try {
            operation.run();
        } finally {
            stop(name);
        }
    }
    
    /**
     * Gets profile data for an operation.
     * 
     * @param name The operation name
     * @return Profile data, or null if not found
     */
    public static ProfileData getProfile(String name) {
        if (name == null) {
            return null;
        }
        return PROFILES.get(name);
    }
    
    /**
     * Gets all profile data.
     * 
     * @return Map of operation names to profile data
     */
    public static Map<String, ProfileData> getAllProfiles() {
        return new HashMap<>(PROFILES);
    }
    
    /**
     * Resets profile data for an operation.
     * 
     * @param name The operation name
     */
    public static void reset(String name) {
        if (name != null) {
            PROFILES.remove(name);
        }
    }
    
    /**
     * Resets all profile data.
     */
    public static void resetAll() {
        PROFILES.clear();
        ACTIVE_PROFILES.clear();
    }
    
    /**
     * Logs profile summary for an operation.
     * 
     * @param name The operation name
     */
    public static void logProfile(String name) {
        ProfileData data = getProfile(name);
        if (data != null) {
            DevLogger.logDebug(PerformanceProfiler.class, "logProfile",
                String.format("Profile '%s': calls=%d, total=%.2fms, avg=%.2fms, min=%.2fms, max=%.2fms",
                    name, data.getCallCount(),
                    data.getTotalTime() / 1_000_000.0,
                    data.getAverageTime() / 1_000_000.0,
                    data.getMinTime() / 1_000_000.0,
                    data.getMaxTime() / 1_000_000.0));
        }
    }
}

