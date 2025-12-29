package at.koopro.spells_n_squares.features.ghosts;

import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

/**
 * Manages persistent storage for ghost data.
 * Ghost data is stored in the entity's NBT, which persists automatically.
 * This manager provides helper methods to update ghost entities when data changes.
 */
public final class GhostStorageManager {
    private GhostStorageManager() {
    }
    
    /**
     * Saves ghost data to storage (updates the entity's NBT).
     * This is a convenience method that updates the entity if it exists in the world.
     * The entity will automatically save the data in its NBT.
     */
    public static void saveGhostData(ServerLevel level, UUID ghostId, GhostData.GhostComponent data) {
        if (level == null || ghostId == null || data == null) {
            return;
        }
        
        // Find the ghost entity and update its data
        // The entity will save it in its NBT automatically
        for (var entity : level.getAllEntities()) {
            if (entity instanceof GhostEntity ghost && ghostId.equals(ghost.getGhostId())) {
                ghost.setGhostData(data);
                return;
            }
        }
    }
    
    /**
     * Loads ghost data from storage.
     * This loads from the entity's NBT if the entity exists, otherwise returns null.
     */
    public static GhostData.GhostComponent loadGhostData(ServerLevel level, UUID ghostId) {
        if (level == null || ghostId == null) {
            return null;
        }
        
        // Try to find the ghost entity and get its data
        for (var entity : level.getAllEntities()) {
            if (entity instanceof GhostEntity ghost && ghostId.equals(ghost.getGhostId())) {
                return ghost.getGhostData();
            }
        }
        
        return null;
    }
}

