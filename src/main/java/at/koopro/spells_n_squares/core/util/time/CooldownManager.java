package at.koopro.spells_n_squares.core.util.time;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized cooldown management system for items, spells, and other game elements.
 * Provides efficient cooldown tracking and querying.
 */
public final class CooldownManager {
    
    private static final Map<UUID, Map<String, CooldownEntry>> PLAYER_COOLDOWNS = new ConcurrentHashMap<>();
    
    private CooldownManager() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents a cooldown entry.
     */
    public static class CooldownEntry {
        private final long startTime;
        private final long duration;
        private final String cooldownId;
        
        public CooldownEntry(String cooldownId, long startTime, long duration) {
            this.cooldownId = cooldownId;
            this.startTime = startTime;
            this.duration = duration;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public long getDuration() {
            return duration;
        }
        
        public String getCooldownId() {
            return cooldownId;
        }
        
        public long getRemainingTime(long currentTime) {
            long elapsed = currentTime - startTime;
            return Math.max(0, duration - elapsed);
        }
        
        public boolean isComplete(long currentTime) {
            return getRemainingTime(currentTime) <= 0;
        }
        
        public float getProgress(long currentTime) {
            if (duration <= 0) {
                return 1.0f;
            }
            long elapsed = currentTime - startTime;
            return Math.min(1.0f, Math.max(0.0f, (float) elapsed / (float) duration));
        }
    }
    
    /**
     * Sets a cooldown for a player.
     * 
     * @param playerId The player's UUID
     * @param cooldownId The cooldown identifier (e.g., "spell:fireball")
     * @param durationTicks The cooldown duration in ticks
     * @param currentTime The current game time in ticks
     * @return true if cooldown was set
     */
    public static boolean setCooldown(UUID playerId, String cooldownId, long durationTicks, long currentTime) {
        if (playerId == null || cooldownId == null || durationTicks < 0) {
            return false;
        }
        
        Map<String, CooldownEntry> cooldowns = PLAYER_COOLDOWNS.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        cooldowns.put(cooldownId, new CooldownEntry(cooldownId, currentTime, durationTicks));
        
        DevLogger.logStateChange(CooldownManager.class, "setCooldown",
            "Set cooldown: " + cooldownId + " for " + durationTicks + " ticks");
        
        return true;
    }
    
    /**
     * Checks if a player has an active cooldown.
     * 
     * @param playerId The player's UUID
     * @param cooldownId The cooldown identifier
     * @param currentTime The current game time in ticks
     * @return true if cooldown is active
     */
    public static boolean hasCooldown(UUID playerId, String cooldownId, long currentTime) {
        if (playerId == null || cooldownId == null) {
            return false;
        }
        
        Map<String, CooldownEntry> cooldowns = PLAYER_COOLDOWNS.get(playerId);
        if (cooldowns == null) {
            return false;
        }
        
        CooldownEntry entry = cooldowns.get(cooldownId);
        if (entry == null) {
            return false;
        }
        
        if (entry.isComplete(currentTime)) {
            cooldowns.remove(cooldownId);
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets the remaining cooldown time.
     * 
     * @param playerId The player's UUID
     * @param cooldownId The cooldown identifier
     * @param currentTime The current game time in ticks
     * @return Remaining time in ticks, or 0 if no cooldown
     */
    public static long getRemainingCooldown(UUID playerId, String cooldownId, long currentTime) {
        if (playerId == null || cooldownId == null) {
            return 0;
        }
        
        Map<String, CooldownEntry> cooldowns = PLAYER_COOLDOWNS.get(playerId);
        if (cooldowns == null) {
            return 0;
        }
        
        CooldownEntry entry = cooldowns.get(cooldownId);
        if (entry == null) {
            return 0;
        }
        
        long remaining = entry.getRemainingTime(currentTime);
        if (remaining <= 0) {
            cooldowns.remove(cooldownId);
            return 0;
        }
        
        return remaining;
    }
    
    /**
     * Gets the cooldown progress (0.0 to 1.0).
     * 
     * @param playerId The player's UUID
     * @param cooldownId The cooldown identifier
     * @param currentTime The current game time in ticks
     * @return Progress from 0.0 (just started) to 1.0 (complete)
     */
    public static float getCooldownProgress(UUID playerId, String cooldownId, long currentTime) {
        if (playerId == null || cooldownId == null) {
            return 1.0f;
        }
        
        Map<String, CooldownEntry> cooldowns = PLAYER_COOLDOWNS.get(playerId);
        if (cooldowns == null) {
            return 1.0f;
        }
        
        CooldownEntry entry = cooldowns.get(cooldownId);
        if (entry == null) {
            return 1.0f;
        }
        
        return entry.getProgress(currentTime);
    }
    
    /**
     * Removes a cooldown (forces it to complete).
     * 
     * @param playerId The player's UUID
     * @param cooldownId The cooldown identifier
     * @return true if cooldown was removed
     */
    public static boolean removeCooldown(UUID playerId, String cooldownId) {
        if (playerId == null || cooldownId == null) {
            return false;
        }
        
        Map<String, CooldownEntry> cooldowns = PLAYER_COOLDOWNS.get(playerId);
        if (cooldowns == null) {
            return false;
        }
        
        CooldownEntry removed = cooldowns.remove(cooldownId);
        if (removed != null) {
            DevLogger.logStateChange(CooldownManager.class, "removeCooldown",
                "Removed cooldown: " + cooldownId);
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets all active cooldowns for a player.
     * 
     * @param playerId The player's UUID
     * @param currentTime The current game time in ticks
     * @return Map of cooldown ID to remaining time
     */
    public static Map<String, Long> getActiveCooldowns(UUID playerId, long currentTime) {
        if (playerId == null) {
            return Collections.emptyMap();
        }
        
        Map<String, CooldownEntry> cooldowns = PLAYER_COOLDOWNS.get(playerId);
        if (cooldowns == null || cooldowns.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<String, Long> active = new HashMap<>();
        List<String> toRemove = new ArrayList<>();
        
        for (Map.Entry<String, CooldownEntry> entry : cooldowns.entrySet()) {
            long remaining = entry.getValue().getRemainingTime(currentTime);
            if (remaining > 0) {
                active.put(entry.getKey(), remaining);
            } else {
                toRemove.add(entry.getKey());
            }
        }
        
        // Clean up completed cooldowns
        for (String id : toRemove) {
            cooldowns.remove(id);
        }
        
        return active;
    }
    
    /**
     * Clears all cooldowns for a player.
     * 
     * @param playerId The player's UUID
     */
    public static void clearCooldowns(UUID playerId) {
        if (playerId != null) {
            PLAYER_COOLDOWNS.remove(playerId);
        }
    }
    
    /**
     * Cleans up expired cooldowns for all players.
     * Should be called periodically to prevent memory leaks.
     * 
     * @param currentTime The current game time in ticks
     */
    public static void cleanupExpiredCooldowns(long currentTime) {
        for (Map.Entry<UUID, Map<String, CooldownEntry>> playerEntry : PLAYER_COOLDOWNS.entrySet()) {
            Map<String, CooldownEntry> cooldowns = playerEntry.getValue();
            List<String> toRemove = new ArrayList<>();
            
            for (Map.Entry<String, CooldownEntry> entry : cooldowns.entrySet()) {
                if (entry.getValue().isComplete(currentTime)) {
                    toRemove.add(entry.getKey());
                }
            }
            
            for (String id : toRemove) {
                cooldowns.remove(id);
            }
            
            // Remove player entry if no cooldowns remain
            if (cooldowns.isEmpty()) {
                PLAYER_COOLDOWNS.remove(playerEntry.getKey());
            }
        }
    }
}

