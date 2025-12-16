package at.koopro.spells_n_squares.core.api;

import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import at.koopro.spells_n_squares.features.playerclass.PlayerClassManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Wrapper class that implements IPlayerClassManager by delegating to PlayerClassManager static methods.
 * This provides the interface abstraction while maintaining backward compatibility with existing static methods.
 */
public final class PlayerClassManagerWrapper implements IPlayerClassManager {
    public static final PlayerClassManagerWrapper INSTANCE = new PlayerClassManagerWrapper();
    
    private PlayerClassManagerWrapper() {
        // Singleton instance
    }
    
    @Override
    public void setPlayerClass(Player player, PlayerClass playerClass) {
        PlayerClassManager.setPlayerClass(player, playerClass);
    }
    
    @Override
    public PlayerClass getPlayerClass(Player player) {
        return PlayerClassManager.getPlayerClass(player);
    }
    
    @Override
    public boolean hasPlayerClass(Player player, PlayerClass playerClass) {
        return PlayerClassManager.hasPlayerClass(player, playerClass);
    }
    
    @Override
    public void clearPlayerData(Player player) {
        PlayerClassManager.clearPlayerData(player);
    }
    
    @Override
    public void syncPlayerClassToClient(ServerPlayer serverPlayer) {
        PlayerClassManager.syncPlayerClassToClient(serverPlayer);
    }
}

