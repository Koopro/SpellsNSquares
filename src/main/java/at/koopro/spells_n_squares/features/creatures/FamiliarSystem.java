package at.koopro.spells_n_squares.features.creatures;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * System for managing familiars (tamed companion creatures).
 */
public final class FamiliarSystem {
    private static final Map<UUID, UUID> PLAYER_FAMILIARS = new HashMap<>(); // Player UUID -> Familiar Entity UUID
    
    private FamiliarSystem() {
    }
    
    /**
     * Sets a creature as a player's familiar.
     * @param player The player
     * @param familiarEntityId The entity UUID of the familiar
     */
    public static void setFamiliar(Player player, UUID familiarEntityId) {
        if (player instanceof ServerPlayer) {
            PLAYER_FAMILIARS.put(player.getUUID(), familiarEntityId);
        }
    }
    
    /**
     * Gets a player's familiar entity ID.
     * @param player The player
     * @return The familiar entity UUID, or null if none
     */
    public static UUID getFamiliar(Player player) {
        return PLAYER_FAMILIARS.get(player.getUUID());
    }
    
    /**
     * Removes a player's familiar.
     * @param player The player
     */
    public static void removeFamiliar(Player player) {
        PLAYER_FAMILIARS.remove(player.getUUID());
    }
    
    /**
     * Checks if a player has a familiar.
     * @param player The player
     * @return True if the player has a familiar
     */
    public static boolean hasFamiliar(Player player) {
        return PLAYER_FAMILIARS.containsKey(player.getUUID());
    }
}






