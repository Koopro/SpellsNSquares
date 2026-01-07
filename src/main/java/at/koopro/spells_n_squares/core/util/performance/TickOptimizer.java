package at.koopro.spells_n_squares.core.util.performance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tick-based optimization utilities.
 * Provides methods for optimizing tick-based operations.
 */
public final class TickOptimizer {
    
    private static final Map<String, Integer> TICK_COUNTERS = new ConcurrentHashMap<>();
    private static final Map<String, Integer> TICK_INTERVALS = new ConcurrentHashMap<>();
    
    private TickOptimizer() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if an operation should run this tick based on interval.
     * 
     * @param operationId The operation identifier
     * @param intervalTicks The interval in ticks
     * @param currentTick The current game tick
     * @return true if operation should run
     */
    public static boolean shouldRun(String operationId, int intervalTicks, long currentTick) {
        if (operationId == null || intervalTicks <= 0) {
            return false;
        }
        
        Integer lastTick = TICK_COUNTERS.get(operationId);
        if (lastTick == null) {
            TICK_COUNTERS.put(operationId, (int) currentTick);
            TICK_INTERVALS.put(operationId, intervalTicks);
            return true;
        }
        
        int interval = TICK_INTERVALS.getOrDefault(operationId, intervalTicks);
        if (currentTick - lastTick >= interval) {
            TICK_COUNTERS.put(operationId, (int) currentTick);
            return true;
        }
        
        return false;
    }
    
    /**
     * Resets the tick counter for an operation.
     * 
     * @param operationId The operation identifier
     */
    public static void reset(String operationId) {
        if (operationId != null) {
            TICK_COUNTERS.remove(operationId);
            TICK_INTERVALS.remove(operationId);
        }
    }
    
    /**
     * Clears all tick counters.
     */
    public static void clearAll() {
        TICK_COUNTERS.clear();
        TICK_INTERVALS.clear();
    }
}

