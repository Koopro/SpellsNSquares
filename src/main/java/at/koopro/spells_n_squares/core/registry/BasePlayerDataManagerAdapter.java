package at.koopro.spells_n_squares.core.registry;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Base class for player data manager adapters.
 * Provides common functionality for adapters that wrap static manager methods.
 */
public abstract class BasePlayerDataManagerAdapter implements PlayerDataManager {
    
    @Override
    public abstract void clearPlayerData(Player player);
    
    @Override
    public void syncToClient(ServerPlayer serverPlayer) {
        // Default implementation does nothing - override if sync is needed
    }
}


