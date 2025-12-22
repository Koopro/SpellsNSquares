package at.koopro.spells_n_squares.features.magic;

import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import at.koopro.spells_n_squares.features.playerclass.PlayerClassManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Manages Animagus transformations for players.
 * Handles registration, form selection, and transformation mechanics.
 */
public final class AnimagusSystem {
    private AnimagusSystem() {
    }
    
    // Registry of registered animagi (player UUID -> AnimagusForm)
    private static final Map<UUID, AnimagusForm> registeredAnimagi = new HashMap<>();
    
    // Active transformations (player UUID -> isTransformed)
    private static final Map<UUID, Boolean> activeTransformations = new HashMap<>();
    
    /**
     * Represents an Animagus form.
     */
    public record AnimagusForm(
        AnimalForm form,
        long registrationDate,
        String registrationMethod // "spell", "item", "command", etc.
    ) {}
    
    /**
     * Available animal forms for Animagus transformation.
     */
    public enum AnimalForm {
        CAT("Cat", "Transforms into a cat"),
        DOG("Dog", "Transforms into a dog"),
        BIRD("Bird", "Transforms into a bird"),
        RAT("Rat", "Transforms into a rat"),
        STAG("Stag", "Transforms into a stag"),
        OTTER("Otter", "Transforms into an otter");
        
        private final String displayName;
        private final String description;
        
        AnimalForm(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Registers a player as an Animagus.
     */
    public static boolean registerAnimagus(Player player, AnimalForm form, String registrationMethod) {
        UUID playerId = player.getUUID();
        
        if (registeredAnimagi.containsKey(playerId)) {
            return false; // Already registered
        }
        
        registeredAnimagi.put(playerId, new AnimagusForm(form, System.currentTimeMillis(), registrationMethod));
        
        // Add ANIMAGUS player class if it exists (will be added to PlayerClass enum)
        // For now, we'll track it separately
        
        return true;
    }
    
    /**
     * Checks if a player is a registered Animagus.
     */
    public static boolean isAnimagus(Player player) {
        return registeredAnimagi.containsKey(player.getUUID());
    }
    
    /**
     * Gets the Animagus form for a player.
     */
    public static AnimagusForm getAnimagusForm(Player player) {
        return registeredAnimagi.get(player.getUUID());
    }
    
    /**
     * Transforms a player into their Animagus form.
     */
    public static boolean transform(ServerPlayer player) {
        if (!isAnimagus(player)) {
            return false;
        }
        
        AnimagusForm form = getAnimagusForm(player);
        if (form == null) {
            return false;
        }
        
        // TODO: Implement actual transformation (spawn animal entity, hide player, etc.)
        activeTransformations.put(player.getUUID(), true);
        
        return true;
    }
    
    /**
     * Reverts a player from their Animagus form.
     */
    public static boolean revert(ServerPlayer player) {
        UUID playerId = player.getUUID();
        
        if (!activeTransformations.getOrDefault(playerId, false)) {
            return false; // Not transformed
        }
        
        // TODO: Implement actual reversion (remove animal entity, show player, etc.)
        activeTransformations.put(playerId, false);
        
        return true;
    }
    
    /**
     * Checks if a player is currently transformed.
     */
    public static boolean isTransformed(Player player) {
        return activeTransformations.getOrDefault(player.getUUID(), false);
    }
    
    /**
     * Clears Animagus data for a player (on disconnect).
     */
    public static void clearPlayerData(Player player) {
        UUID playerId = player.getUUID();
        registeredAnimagi.remove(playerId);
        activeTransformations.remove(playerId);
    }
}






