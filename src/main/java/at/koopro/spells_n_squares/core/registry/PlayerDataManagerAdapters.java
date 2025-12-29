package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.features.playerclass.PlayerClassManager;
import at.koopro.spells_n_squares.features.spell.LumosManager;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Adapter classes that wrap static manager methods to implement PlayerDataManager interface.
 * This allows existing managers to work with the registry system without refactoring.
 */
public class PlayerDataManagerAdapters {
    
    /**
     * Adapter for SpellManager.
     */
    public static class SpellManagerAdapter implements PlayerDataManager {
        @Override
        public void clearPlayerData(Player player) {
            SpellManager.clearPlayerData(player);
        }
        
        @Override
        public void syncToClient(ServerPlayer serverPlayer) {
            SpellManager.syncSpellSlotsToClient(serverPlayer);
            SpellManager.syncCooldownsToClient(serverPlayer);
        }
    }
    
    /**
     * Adapter for PlayerClassManager.
     */
    public static class PlayerClassManagerAdapter implements PlayerDataManager {
        @Override
        public void clearPlayerData(Player player) {
            PlayerClassManager.clearPlayerData(player);
        }
        
        @Override
        public void syncToClient(ServerPlayer serverPlayer) {
            // Load classes from data component into memory cache
            PlayerClassManager.loadPlayerClasses(serverPlayer);
            // Sync to client
            PlayerClassManager.syncPlayerClassToClient(serverPlayer);
        }
    }
    
    /**
     * Adapter for LumosManager.
     */
    public static class LumosManagerAdapter implements PlayerDataManager {
        @Override
        public void clearPlayerData(Player player) {
            LumosManager.clearPlayerData(player);
        }
        
        // LumosManager doesn't need sync as it uses item data components
    }
}





















