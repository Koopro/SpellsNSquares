package at.koopro.spells_n_squares.features.playerclass;

import at.koopro.spells_n_squares.core.api.addon.events.AddonEventBus;
import at.koopro.spells_n_squares.core.api.addon.events.PlayerClassChangeEvent;
import at.koopro.spells_n_squares.features.playerclass.network.PlayerClassSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages player classes for all players.
 * Supports multiple classes per player with conflict checking.
 * Handles storage, retrieval, and syncing of player class data.
 */
public class PlayerClassManager {
    // Per-player class assignments (multiple classes per player)
    // Using HashMap - order doesn't matter, O(1) lookup needed
    private static final Map<Player, Set<PlayerClass>> playerClasses = new HashMap<>();
    
    /**
     * Sets the class for a player (backward compatibility method).
     * This adds the class rather than replacing all classes.
     * @param player The player
     * @param playerClass The class to assign
     */
    public static void setPlayerClass(Player player, PlayerClass playerClass) {
        if (playerClass == null || playerClass == PlayerClass.NONE) {
            return;
        }
        
        // Get old classes for event
        Set<PlayerClass> oldClasses = getPlayerClasses(player);
        
        // Add the class (will check conflicts)
        addPlayerClass(player, playerClass);
        
        // Fire event if classes changed
        Set<PlayerClass> newClasses = getPlayerClasses(player);
        if (!oldClasses.equals(newClasses)) {
            PlayerClass oldPrimary = getPrimaryClass(oldClasses);
            PlayerClass newPrimary = getPrimaryClass(newClasses);
            if (oldPrimary != newPrimary) {
                PlayerClassChangeEvent event = new PlayerClassChangeEvent(player, oldPrimary, newPrimary);
                AddonEventBus.getInstance().post(event);
            }
        }
        
        // Sync to client if this is a server player
        ServerPlayer serverPlayer = at.koopro.spells_n_squares.core.util.PlayerValidationUtils.asServerPlayer(player);
        if (serverPlayer != null) {
            syncPlayerClassToClient(serverPlayer);
        }
    }
    
    /**
     * Gets the primary class for a player (backward compatibility method).
     * @param player The player
     * @return The player's primary class, or NONE if not set
     */
    public static PlayerClass getPlayerClass(Player player) {
        return getPrimaryClass(getPlayerClasses(player));
    }
    
    /**
     * Gets all classes for a player.
     * First checks in-memory cache, then falls back to data component.
     * @param player The player
     * @return A set of all player's classes (never null, may be empty)
     */
    public static Set<PlayerClass> getPlayerClasses(Player player) {
        // Check in-memory cache first
        Set<PlayerClass> cached = playerClasses.get(player);
        if (cached != null) {
            return new HashSet<>(cached);
        }
        
        // Fall back to data component
        return PlayerClassData.getClasses(player);
    }
    
    /**
     * Adds a class to a player.
     * Checks for conflicts before adding.
     * @param player The player
     * @param playerClass The class to add
     * @return true if the class was added, false if it couldn't be added (conflict or duplicate)
     */
    public static boolean addPlayerClass(Player player, PlayerClass playerClass) {
        return addPlayerClass(player, playerClass, "unknown");
    }
    
    /**
     * Adds a class to a player with acquisition source.
     * Checks for conflicts before adding.
     * @param player The player
     * @param playerClass The class to add
     * @param acquiredBy How the class was acquired (spell name, item, command, etc.)
     * @return true if the class was added, false if it couldn't be added (conflict or duplicate)
     */
    public static boolean addPlayerClass(Player player, PlayerClass playerClass, String acquiredBy) {
        if (playerClass == null || playerClass == PlayerClass.NONE) {
            return false;
        }
        
        Set<PlayerClass> classes = getPlayerClasses(player);
        
        // Check if already has this class
        if (classes.contains(playerClass)) {
            return false;
        }
        
        // Check for conflicts
        if (!ClassConflictChecker.canAddClass(playerClass, classes)) {
            return false;
        }
        
        // Add to in-memory cache
        playerClasses.put(player, new HashSet<>(classes));
        classes.add(playerClass);
        
        // Persist to data component
        PlayerClassData.addClass(player, playerClass, acquiredBy);
        
        // Sync to client if this is a server player
        ServerPlayer serverPlayer = at.koopro.spells_n_squares.core.util.PlayerValidationUtils.asServerPlayer(player);
        if (serverPlayer != null) {
            syncPlayerClassToClient(serverPlayer);
        }
        
        return true;
    }
    
    /**
     * Removes a class from a player.
     * @param player The player
     * @param playerClass The class to remove
     * @return true if the class was removed, false if player didn't have it
     */
    public static boolean removePlayerClass(Player player, PlayerClass playerClass) {
        if (playerClass == null || playerClass == PlayerClass.NONE) {
            return false;
        }
        
        Set<PlayerClass> classes = getPlayerClasses(player);
        if (!classes.contains(playerClass)) {
            return false;
        }
        
        // Remove from in-memory cache
        classes.remove(playerClass);
        if (classes.isEmpty()) {
            playerClasses.remove(player);
        } else {
            playerClasses.put(player, new HashSet<>(classes));
        }
        
        // Persist to data component
        PlayerClassData.removeClass(player, playerClass);
        
        // Sync to client if this is a server player
        ServerPlayer serverPlayer = at.koopro.spells_n_squares.core.util.PlayerValidationUtils.asServerPlayer(player);
        if (serverPlayer != null) {
            syncPlayerClassToClient(serverPlayer);
        }
        
        return true;
    }
    
    /**
     * Checks if a player has a specific class.
     * @param player The player
     * @param playerClass The class to check for
     * @return true if the player has the class
     */
    public static boolean hasPlayerClass(Player player, PlayerClass playerClass) {
        return getPlayerClasses(player).contains(playerClass);
    }
    
    /**
     * Checks if a player has any of the specified classes.
     * @param player The player
     * @param classes The classes to check for
     * @return true if the player has any of the classes
     */
    public static boolean hasAnyClass(Player player, Set<PlayerClass> classes) {
        Set<PlayerClass> playerClasses = getPlayerClasses(player);
        for (PlayerClass clazz : classes) {
            if (playerClasses.contains(clazz)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if a player has all of the specified classes.
     * @param player The player
     * @param classes The classes to check for
     * @return true if the player has all of the classes
     */
    public static boolean hasAllClasses(Player player, Set<PlayerClass> classes) {
        return getPlayerClasses(player).containsAll(classes);
    }
    
    /**
     * Checks if a class can be added to a player.
     * @param player The player
     * @param playerClass The class to check
     * @return true if the class can be added
     */
    public static boolean canAddClass(Player player, PlayerClass playerClass) {
        return ClassConflictChecker.canAddClass(playerClass, getPlayerClasses(player));
    }
    
    /**
     * Gets the primary class for a player.
     * Primary class is determined by priority: BASE > ROLE > TRANSFORMATION > ALIGNMENT > ORGANIZATION > BLOOD_STATUS
     * @param player The player
     * @return The primary class, or NONE if player has no classes
     */
    public static PlayerClass getPrimaryClass(Player player) {
        return getPrimaryClass(getPlayerClasses(player));
    }
    
    /**
     * Gets the primary class from a set of classes.
     */
    private static PlayerClass getPrimaryClass(Set<PlayerClass> classes) {
        if (classes == null || classes.isEmpty()) {
            return PlayerClass.NONE;
        }
        
        // Priority order: BASE > ROLE > TRANSFORMATION > ALIGNMENT > ORGANIZATION > BLOOD_STATUS
        ClassCategory[] priority = {
            ClassCategory.BASE,
            ClassCategory.ROLE,
            ClassCategory.TRANSFORMATION,
            ClassCategory.ALIGNMENT,
            ClassCategory.ORGANIZATION,
            ClassCategory.BLOOD_STATUS
        };
        
        for (ClassCategory category : priority) {
            for (PlayerClass clazz : classes) {
                if (clazz.getCategory() == category) {
                    return clazz;
                }
            }
        }
        
        // Fallback: return first class
        return classes.iterator().next();
    }
    
    /**
     * Gets classes by category for a player.
     * @param player The player
     * @param category The category to filter by
     * @return Set of classes in the specified category
     */
    public static Set<PlayerClass> getClassesByCategory(Player player, ClassCategory category) {
        return getPlayerClasses(player).stream()
            .filter(clazz -> clazz.getCategory() == category)
            .collect(Collectors.toSet());
    }
    
    /**
     * Clears the class data for a player (used when player disconnects).
     * Only clears in-memory cache; data component persists.
     * @param player The player
     */
    public static void clearPlayerData(Player player) {
        playerClasses.remove(player);
    }
    
    /**
     * Loads player classes from data component into memory cache.
     * Call this when a player joins the server.
     * @param player The player
     */
    public static void loadPlayerClasses(Player player) {
        Set<PlayerClass> classes = PlayerClassData.getClasses(player);
        if (!classes.isEmpty()) {
            playerClasses.put(player, new HashSet<>(classes));
        }
    }
    
    /**
     * Syncs player classes to the client for a server player.
     * @param serverPlayer The server player
     */
    public static void syncPlayerClassToClient(ServerPlayer serverPlayer) {
        Set<PlayerClass> classes = getPlayerClasses(serverPlayer);
        PlayerClassSyncPayload payload = new PlayerClassSyncPayload(classes);
        PacketDistributor.sendToPlayer(serverPlayer, payload);
    }
}

