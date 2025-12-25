package at.koopro.spells_n_squares.core.api;

import at.koopro.spells_n_squares.features.spell.SpellManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Wrapper class that implements ISpellManager by delegating to SpellManager static methods.
 * This provides the interface abstraction while maintaining backward compatibility with existing static methods.
 */
public final class SpellManagerWrapper implements ISpellManager {
    public static final SpellManagerWrapper INSTANCE = new SpellManagerWrapper();
    
    private SpellManagerWrapper() {
        // Singleton instance
    }
    
    @Override
    public void setSpellInSlot(Player player, int slot, Identifier spellId) {
        SpellManager.setSpellInSlot(player, slot, spellId);
    }
    
    @Override
    public Identifier getSpellInSlot(Player player, int slot) {
        return SpellManager.getSpellInSlot(player, slot);
    }
    
    @Override
    public boolean castSpellInSlot(Player player, Level level, int slot) {
        return SpellManager.castSpellInSlot(player, level, slot);
    }
    
    @Override
    public void setCooldown(Player player, Identifier spellId, int ticks) {
        SpellManager.setCooldown(player, spellId, ticks);
    }
    
    @Override
    public boolean isOnCooldown(Player player, Identifier spellId) {
        return SpellManager.isOnCooldown(player, spellId);
    }
    
    @Override
    public int getRemainingCooldown(Player player, Identifier spellId) {
        return SpellManager.getRemainingCooldown(player, spellId);
    }
    
    @Override
    public void tickCooldowns(Player player) {
        SpellManager.tickCooldowns(player);
    }
    
    @Override
    public void clearPlayerData(Player player) {
        SpellManager.clearPlayerData(player);
    }
    
    @Override
    public void syncSpellSlotsToClient(ServerPlayer serverPlayer) {
        SpellManager.syncSpellSlotsToClient(serverPlayer);
    }
    
    @Override
    public void syncCooldownsToClient(ServerPlayer serverPlayer) {
        SpellManager.syncCooldownsToClient(serverPlayer);
    }
}

















